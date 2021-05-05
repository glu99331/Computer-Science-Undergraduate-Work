import collections
import copy
import os
import matplotlib.pyplot as plt
import seaborn as sns
import torch
import torch.nn as nn
from absl import app, flags
from skimage import io
from torch.utils.data import DataLoader, Dataset
from torch.utils.tensorboard import SummaryWriter
from torchvision import transforms
from tqdm import tqdm

FLAGS = flags.FLAGS

flags.DEFINE_enum('task_type', 'training', ['training', 'analysis'],
                  'Specifies the task type.')

# Hyperparameters for Part I
flags.DEFINE_float('learning_rate', 1e-3, 'Learning rate.')
flags.DEFINE_float('weight_decay', 0, 'Weight decay (L2 regularization).')
flags.DEFINE_integer('batch_size', 128, 'Number of examples per batch.')
flags.DEFINE_integer('epochs', 100, 'Number of epochs for training.')
flags.DEFINE_string('experiment_name', 'exp', 'Defines experiment name.')
flags.DEFINE_enum('label_type', 'domain', ['domain', 'category'],
                  'Specifies prediction task.')

# Hyperparemeters for Part III
flags.DEFINE_string('model_checkpoint', '',
                    'Specifies the checkpont for analyzing.')

LABEL_SIZE = {'domain': 4, 'category': 7}


class PACSDataset(Dataset):

  def __init__(self,
               root_dir,
               label_type='domain',
               is_training=False,
               transform=None):
    self.root_dir = os.path.join(root_dir, 'train' if is_training else 'val')
    self.label_type = label_type
    self.is_training = is_training
    if transform:
      self.transform = transform
    else:
      self.transform = transforms.Compose([
          transforms.ToTensor(),
          transforms.Normalize(mean=[0.7659, 0.7463, 0.7173],
                               std=[0.3089, 0.3181, 0.3470]),
      ])

    self.dataset, self.label_list = self.initialize_dataset()
    self.label_to_id = {x: i for i, x in enumerate(self.label_list)}
    self.id_to_label = {i: x for i, x in enumerate(self.label_list)}

  def __len__(self):
    return len(self.dataset)

  def __getitem__(self, idx):
    image, label = self.dataset[idx]
    label_id = self.label_to_id[label]
    image = self.transform(image)
    return image, label_id

  def initialize_dataset(self):
    assert os.path.isdir(self.root_dir), \
        '`root_dir` is not found at %s' % self.root_dir

    dataset = []
    domain_set = set()
    category_set = set()
    cnt = 0

    for root, dirs, files in os.walk(self.root_dir, topdown=True):
      if files:
        _, domain, category = root.rsplit('/', maxsplit=2)
        domain_set.add(domain)
        category_set.add(category)
        pbar = tqdm(files)
        for name in pbar:
          pbar.set_description('Processing Folder: domain=%s, category=%s' %
                               (domain, category))
          img_array = io.imread(os.path.join(root, name))
          dataset.append((img_array, domain, category))

    images, domains, categories = zip(*dataset)

    if self.label_type == 'domain':
      labels = sorted(domain_set)
      dataset = list(zip(images, domains))
    elif self.label_type == 'category':
      labels = sorted(category_set)
      dataset = list(zip(images, categories))
    else:
      raise ValueError(
          'Unknown `label_type`: Expecting `domain` or `category`.')

    return dataset, labels


class AlexNet(nn.Module):

  def __init__(self, configs):
    super().__init__()
    self.configs = configs
# ================================================================================================================
#     Layer (type)      Kernel      Padding      Stride      Dilation          Output Shape           Param #     
# ----------------------------------------------------------------------------------------------------------------
#         Conv2d-1     11 x 11                        4                    [-1, 96, 55, 55]            34,944
#           ReLU-2                                                         [-1, 96, 55, 55]                 0
#      MaxPool2d-3           3                        2                    [-1, 96, 27, 27]                 0
#         Conv2d-4       5 x 5            2                               [-1, 256, 27, 27]           614,656
#           ReLU-5                                                        [-1, 256, 27, 27]                 0
#      MaxPool2d-6           3                        2                   [-1, 256, 13, 13]                 0
#         Conv2d-7       3 x 3            1                               [-1, 384, 13, 13]           885,120
#           ReLU-8                                                        [-1, 384, 13, 13]                 0
#         Conv2d-9       3 x 3            1                               [-1, 384, 13, 13]         1,327,488
#          ReLU-10                                                        [-1, 384, 13, 13]                 0
#        Conv2d-11       3 x 3            1                               [-1, 256, 13, 13]           884,992
#          ReLU-12                                                        [-1, 256, 13, 13]                 0
#     MaxPool2d-13           3                        2                     [-1, 256, 6, 6]                 0
#       Flatten-14                                                               [-1, 9216]                 0
#       Dropout-15                                                               [-1, 9216]                 0
#        Linear-16                                                               [-1, 4096]        37,752,832
#          ReLU-17                                                               [-1, 4096]                 0
#       Dropout-18                                                               [-1, 4096]                 0
#        Linear-19                                                               [-1, 4096]        16,781,312
#          ReLU-20                                                               [-1, 4096]                 0
#        Linear-21                                                                  [-1, 4]            16,388
# ================================================================================================================
    #define features, classifiers, and flatten by above table
    self.features = nn.Sequential(
      nn.Conv2d(3, 96, kernel_size=11, stride=4),       
      nn.ReLU(inplace = True),                           
      nn.MaxPool2d(kernel_size=3, stride=2),           
      nn.Conv2d(96, 256, kernel_size=5, padding=2),     
      nn.ReLU(inplace = True),                            
      nn.MaxPool2d(kernel_size=3, stride=2),            
      nn.Conv2d(256, 384, kernel_size=3, padding=1),    
      nn.ReLU(inplace = True),                            
      nn.Conv2d(384, 384, kernel_size=3, padding=1),    
      nn.ReLU(inplace = True),                            
      nn.Conv2d(384, 256, kernel_size=3, padding=1),    
      nn.ReLU(inplace = True),                            
      nn.MaxPool2d(kernel_size=3, stride=2)
    )
    self.flatten = nn.Flatten()
    self.classifiers = nn.Sequential(
        nn.Dropout(),
        nn.Linear(9216, 4096),
        nn.ReLU(inplace = True),
        nn.Dropout(),
        nn.Linear(4096, 4096),
        nn.ReLU(inplace = True),
        nn.Linear(4096, configs['num_classes'])
    )            

  def forward(self, x):
    x = self.features(x)
    x = self.flatten(x)
    x = self.classifiers(x)
    return x


def visualize_kernels(kernel_name,
                      kernel_weight,
                      max_in_channels=12,
                      max_out_channels=12,
                      saving_prefix='kernel'):
  """A helper function to visualize the learned convolutional kernels.
  
  Args:
    kernel_name: str, the name of the kernel being visualized. It will be used
        as the filename in the saved figures.
    kernel_weight: torch.Tensor or np.ndarray, the weights of convolutional
        kernel. The shape should be
        [out_channels, in_channels, kernel_height, kernel_width].
    max_in_channels: int, optional, the max in_channels in the visualization.
    max_out_channels: int, optional, the max out_channels in the visualization.
    saving_prefix: str, optional, the directory for saving the visualization.
  """
  print('Visualize the learned filter of `%s`' % kernel_name)
  if isinstance(kernel_weight, torch.Tensor):
    kernel_weight = kernel_weight.cpu().numpy()

  kernel_shape = list(kernel_weight.shape)

  nrows = min(max_in_channels, kernel_shape[1])
  ncols = min(max_out_channels, kernel_shape[0])

  fig, axes = plt.subplots(nrows, ncols, figsize=(ncols, nrows))

  for r in range(nrows):
    for c in range(ncols):
      kernel = kernel_weight[c, r, :, :]
      vmin, vmax = kernel.min(), kernel.max()
      normalized_kernel = (kernel - vmin) / (vmax - vmin)
      sns.heatmap(normalized_kernel,
                  cbar=False,
                  square=True,
                  xticklabels=False,
                  yticklabels=False,
                  ax=axes[r, c])

  plt.xlabel('First %d In-Channels' % nrows)
  plt.ylabel('First %d Out-Channels' % ncols)

  plt.tight_layout()
  plt.savefig(os.path.join(saving_prefix, kernel_name.lower() + '.png'))
  return


def analyze_model_kernels():
  checkpoint = torch.load(FLAGS.model_checkpoint)
  weight_name_to_layer_name = {
    'features.0.weight': 'conv2d-1',
    'features.3.weight': 'conv2d-4',
    'features.6.weight': 'conv2d-7',
    'features.8.weight': 'conv2d-9',
    'features.10.weight': 'conv2d-11'
  }
  for weight_name, kernel_name in weight_name_to_layer_name.items():
    kernel = checkpoint[weight_name]
    n = 'kernel/'+FLAGS.label_type
    visualize_kernels(kernel_name, kernel, saving_prefix=n)


def model_training():
  train_dataset = PACSDataset(root_dir='pacs_dataset',
                              label_type=FLAGS.label_type,
                              is_training=True)
  train_loader = DataLoader(train_dataset,
                            batch_size=FLAGS.batch_size,
                            shuffle=True,
                            num_workers=4)

  val_dataset = PACSDataset(root_dir='pacs_dataset',
                            label_type=FLAGS.label_type,
                            is_training=False)
  val_loader = DataLoader(val_dataset,
                          batch_size=FLAGS.batch_size,
                          shuffle=False,
                          num_workers=4)

  best_model = None
  best_acc = 0.0

  device = torch.device('cuda:0' if torch.cuda.is_available() else 'cpu')

  experiment_name = 'experiments/{}/{}_lr_{}.wd_{}'.format(
      FLAGS.experiment_name, FLAGS.label_type, FLAGS.learning_rate,
      FLAGS.weight_decay)

  os.makedirs(experiment_name, exist_ok=True)
  writer = SummaryWriter(log_dir=experiment_name)

  configs = {'num_classes': LABEL_SIZE[FLAGS.label_type]}

  ############################################################################
  """After implementing all required models, you can switch from here."""
  model = AlexNet(configs).to(device)
  # model = AlexNetLargeKernel(configs).to(device)
  # model = AlexNetAvgPooling(configs).to(device)
  ############################################################################

  print('Model Architecture:\n%s' % model)

  criterion = nn.CrossEntropyLoss(reduction='mean')
  optimizer = torch.optim.Adam(model.parameters(),
                               lr=FLAGS.learning_rate,
                               weight_decay=FLAGS.weight_decay)

  try:
    for epoch in range(FLAGS.epochs):
      for phase in ('train', 'eval'):
        if phase == 'train':
          model.train()
          dataset = train_dataset
          data_loader = train_loader
        else:
          model.eval()
          dataset = val_dataset
          data_loader = val_loader

        running_loss = 0.0
        running_corrects = 0

        for step, (images, labels) in enumerate(data_loader):
          images = images.to(device)
          labels = labels.to(device)

          optimizer.zero_grad()

          with torch.set_grad_enabled(phase == 'train'):
            outputs = model(images)
            _, preds = torch.max(outputs, 1)
            loss = criterion(outputs, labels)

            if phase == 'train':
              loss.backward()
              optimizer.step()

              writer.add_scalar('Loss/{}'.format(phase), loss.item(),
                                epoch * len(data_loader) + step)

          running_loss += loss.item() * images.size(0)
          running_corrects += torch.sum(preds == labels.data)

        epoch_loss = running_loss / len(dataset)
        epoch_acc = running_corrects.double() / len(dataset)
        writer.add_scalar('Epoch_Loss/{}'.format(phase), epoch_loss, epoch)
        writer.add_scalar('Epoch_Accuracy/{}'.format(phase), epoch_acc, epoch)
        print('[Epoch %d] %s accuracy: %.4f, loss: %.4f' %
              (epoch + 1, phase, epoch_acc, epoch_loss))

        if phase == 'eval':
          if epoch_acc > best_acc:
            best_acc = epoch_acc
            best_model = copy.deepcopy(model.state_dict())
            torch.save(best_model, os.path.join(experiment_name,
                                                'best_model.pt'))

  except KeyboardInterrupt:
    pass

  return


def main(unused_argvs):
  if FLAGS.task_type == 'training':
    # train that yee yee ass neural net
    model_training()
  elif FLAGS.task_type == 'analysis':
    analyze_model_kernels()
  else:
    raise ValueError('Unknown `task_type`: %s' % FLAGS.task_type)


if __name__ == '__main__':
  app.run(main)
