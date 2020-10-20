import java.util.LinkedList;
import java.util.List;

public class Phase3 {

    final static int addiu = 1,addu = 2,or = 3,beq = 5,bne = 6,slt = 8,lui = 9,ori = 10;

    final static int addiu_opcode = 9, addu_opcode = 33, or_opcode = 37, beq_opcode = 4, bne_opcode = 5, slt_opcode = 42, lui_opcode = 15, ori_opcode = 13;

    private static String signExtender(String base)
    {
       return base.substring(base.length() - 16, base.length());
    }

    private static String RType_Conversion_Helper(Instruction instruct, String RType)
    {
        RType += signExtension(0,6);
        RType += signExtension(instruct.rs,5);
        RType += signExtension(instruct.rt,5);
        RType += signExtension(instruct.rd,5); 
        RType += signExtension(0,5);   
        RType += signExtension(getOpcode(instruct),6); 
        return RType;
    }

    private static String IType_Conversion_Helper(Instruction instruct, String IType)
    {
        IType += signExtension(getOpcode(instruct),6);
        IType += signExtension(instruct.rs,5);
        IType += signExtension(instruct.rt,5);
        IType += signExtension(instruct.immediate,16);
        return IType;
    }

    private static String BinaryToHexParser(String unparsedInstruction, String parsedInstruction)
    {
        parsedInstruction += Integer.toHexString(Integer.parseInt(unparsedInstruction.substring(0,4),2));
        parsedInstruction += Integer.toHexString(Integer.parseInt(unparsedInstruction.substring(4,8),2));
        parsedInstruction += Integer.toHexString(Integer.parseInt(unparsedInstruction.substring(8,12),2));
        parsedInstruction += Integer.toHexString(Integer.parseInt(unparsedInstruction.substring(12,16),2));
        parsedInstruction += Integer.toHexString(Integer.parseInt(unparsedInstruction.substring(16,20),2));
        parsedInstruction += Integer.toHexString(Integer.parseInt(unparsedInstruction.substring(20,24),2));
        parsedInstruction += Integer.toHexString(Integer.parseInt(unparsedInstruction.substring(24,28),2));
        parsedInstruction += Integer.toHexString(Integer.parseInt(unparsedInstruction.substring(28,32),2));
        return parsedInstruction;
    }

    private static int convertInstruction_Helper_I_Type(Instruction instruct)
    {
       return IType_Conversion(instruct);
    }

    private static int convertInstruction_Helper_R_Type(Instruction instruct)
    {
       return RType_Conversion(instruct);
    }

    private static boolean finalBinaryTranslation(List<Integer> binaryToInteger,Instruction instruct)
    {
        return binaryToInteger.add(Integer.valueOf(convertInstruction(instruct)));
    }

    private static int getOpcode(Instruction instruct)
    {
        switch(instruct.instruction_id)
        {
            case addiu:
                return addiu_opcode;
            case addu:
                return addu_opcode;
            case or:
                return or_opcode;
            case beq:
                return beq_opcode;
            case bne:
                return bne_opcode;
            case slt:
                return slt_opcode;
            case lui:
                return lui_opcode;
            case ori:
                return ori_opcode;
            default:
                throw new AssertionError("You've reached some unreachable code??");
        }
    }

    private static String signExtension(int initial, int desiredSize)
    {
        String stringBase = Integer.toBinaryString(initial);
        int iterationCounter = stringBase.length();

        if(stringBase.length() > desiredSize)
        {
            return signExtender(stringBase);
        }

        for(; iterationCounter < desiredSize; iterationCounter++)
        {
            if (initial < 0)
            {
                stringBase = "1" + stringBase;
            }
            stringBase = "0" + stringBase;        
        }
        
        return stringBase;
    }

    private static int RType_Conversion(Instruction instruct){
        String convertedRType = "";
        convertedRType = RType_Conversion_Helper(instruct, convertedRType);
        return BinaryToHexStringConversion(convertedRType);
    }

    private static int IType_Conversion(Instruction instruct){
        String convertedIType = "";
        convertedIType = IType_Conversion_Helper(instruct, convertedIType);
        return BinaryToHexStringConversion(convertedIType);
    }

    private static int BinaryToHexStringConversion(String unparsedInstruction){
        String parsedInstruction = "";
        parsedInstruction = BinaryToHexParser(unparsedInstruction, parsedInstruction);
        return Integer.parseInt(parsedInstruction,16);
    }
    
    private static int convertInstruction(Instruction instruct){
       switch(instruct.instruction_id)
       {
            case addiu:
                return convertInstruction_Helper_I_Type(instruct);
            case addu:
                return convertInstruction_Helper_R_Type(instruct);
            case or:
                return convertInstruction_Helper_R_Type(instruct);
            case beq:
                return convertInstruction_Helper_I_Type(instruct);
            case bne:
                return convertInstruction_Helper_I_Type(instruct);
            case slt:
                return convertInstruction_Helper_R_Type(instruct);
            case lui:
                return convertInstruction_Helper_I_Type(instruct);
            case ori:
                return convertInstruction_Helper_I_Type(instruct);
            default:
                return 0;
        } 
    }

    public static List<Integer> translate_instructions(List<Instruction> tals) 
    {
        List<Integer> binaryToInteger = new LinkedList<Integer>();
        for(Instruction instr : tals)
        {
            finalBinaryTranslation(binaryToInteger, instr);
        }
        return binaryToInteger;
    }
}