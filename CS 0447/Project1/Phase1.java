
import java.util.LinkedList;
import java.util.List;

public class Phase1
{
    public static List<Instruction> mal_to_tal(List<Instruction> mals) {
        List<Instruction> tals = new LinkedList<Instruction>();

        for(int i = 0; i < mals.size(); i++)
        {
            int immediate = mals.get(i).immediate;
            switch(mals.get(i).instruction_id)
            {
                case 1: //it's addiu 
                if(immediate > 16)
                {
                    if((immediate & 0xFFFF) != immediate)
                    {
                        int lower_immediate = immediate&0x0000FFFF;
                        int upper_immediate = immediate&0xFFFF0000;
                        upper_immediate = upper_immediate>>16;
                        
                        Instruction lui = new Instruction(9,0,0,1,upper_immediate,0,0,mals.get(i).label_id,0);
                        Instruction ori = new Instruction(10,0,1,1,lower_immediate,0,0,0, 0);
                        Instruction addu = new Instruction(2,mals.get(i).rt,mals.get(i).rs,1,0,0,0,0,0);

                        tals.add(lui);
                        tals.add(ori);
                        tals.add(addu);
                    }
                }
                    else
                    {
                        tals.add(mals.get(i));
                    }
                break;
                
                case 10: //it's ori
                if(immediate > 16)
                {
                    if((immediate & 0xFFFF) != immediate)
                    {
                        int lower_immediate = immediate&0x0000FFFF;
                        int upper_immediate = immediate&0xFFFF0000;
                        upper_immediate = upper_immediate>>16;
                        Instruction lui = new Instruction(9,0,0,1,upper_immediate,0,0,mals.get(i).label_id,0);
                        Instruction ori = new Instruction(10,0,1,1,lower_immediate,0,0,0, 0);
                        Instruction or = new Instruction(3, mals.get(i).rt, mals.get(i).rs, 1, 0, 0, 0, 0, 0);

                        tals.add(lui);
                        tals.add(ori);
                        tals.add(or);
                    }
                }
                    else
                    {
                        tals.add(mals.get(i));
                    }
                break;

                case 100: //it's blt
                    Instruction slt = new Instruction(8,1,mals.get(i).rs,mals.get(i).rt,0,0,0,mals.get(i).label_id,0);
                    Instruction bne = new Instruction(6,0,1,0,0,0,0,0,mals.get(i).branch_label);
                    tals.add(slt);
                    tals.add(bne);
                break;

                case 101: //it's bge
                    Instruction slt2 = new Instruction(8,1,mals.get(i).rs,mals.get(i).rt, 0,0,0,mals.get(i).label_id,0);
                    Instruction beq = new Instruction(5,0,1,0,0,0,0,0,mals.get(i).branch_label);
                
                    tals.add(slt2);
                    tals.add(beq);
                break;

                default:
                    tals.add(mals.get(i));
            }
        }
        return tals;
    }
}
