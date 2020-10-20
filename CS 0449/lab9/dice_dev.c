/*
 * "dice, world!" minimal kernel module - /dev version
 *
 * Gordon Lu <gol6@pitt.edu>
 *
 */

#include <linux/fs.h>
#include <linux/init.h>
#include <linux/miscdevice.h>
#include <linux/module.h>
#include <linux/random.h>
#include <asm/uaccess.h>

 static int max_value = 6;
 module_param(max_value, int, S_IRUGO|S_IWUSR);
/*
 * dice_read is the function called when a process calls read() on
 * /dev/dice.  It writes "dice, world!" to the buffer passed in the
 * read() call.
 */

static ssize_t dice_read(struct file* file, char* buf, 
			  size_t count, loff_t *ppos)
{
	unsigned char* temp_buf = kmalloc(count, GFP_KERNEL);
	int i;
	get_random_bytes(temp_buf, count);
	
	for(i = 0; i < count; i++)
	{
		temp_buf[i] %= max_value;
	}
	if(copy_to_user(buf, temp_buf, count))
		return -EINVAL;

	*ppos += count;
	kfree(temp_buf);
	return count;
}

/*
 * The only file operation we care about is read.
 */

static const struct file_operations dice_fops = {
	.owner		= THIS_MODULE,
	.read		= dice_read,
};

static struct miscdevice dice_dev = {
	/*
	 * We don't care what minor number we end up with, so tell the
	 * kernel to just pick one.
	 */
	MISC_DYNAMIC_MINOR,
	/*
	 * Name ourselves /dev/dice.
	 */
	"dice",
	/*
	 * What functions to call when a program performs file
	 * operations on the device.
	 */
	&dice_fops
};

static int __init
dice_init(void)
{
	int ret;

	/*
	 * Create the "dice" device in the /sys/class/misc directory.
	 * Udev will automatically create the /dev/dice device using
	 * the default rules.
	 */
	ret = misc_register(&dice_dev);
	if (ret)
		printk(KERN_ERR
		       "Unable to register \"Dice world!\" misc device\n");

	return ret;
}

module_init(dice_init);

static void __exit
dice_exit(void)
{
	misc_deregister(&dice_dev);
}

module_exit(dice_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Gordon Lu <gol6@pitt.edu>");
MODULE_DESCRIPTION("\"Dice world!\" minimal module");
MODULE_VERSION("dev");
