import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Phase2 
{
    final static int beq = 5, bne = 6;
    public static List<Instruction> resolve_addresses(List<Instruction> unresolved, int first_pc) {
        List<Instruction> resolved = new LinkedList<Instruction>(unresolved);
        for(int instructionCounter= 0; instructionCounter < resolved.size(); instructionCounter++)
	    {
		    if(resolved.get(instructionCounter).instruction_id == beq || resolved.get(instructionCounter).instruction_id == bne) 
		    {
			    int branchLabel = resolved.get(instructionCounter).branch_label;
                int regularCounter= 0; 
                
			    for(int branchCounter= 0; branchCounter < resolved.size(); branchCounter++)
			    {
				    if(resolved.get(instructionCounter).label_id == branchLabel)
				    {
					    regularCounter = branchCounter;
					    break;
				    }
			    }
			    int nextAddress = 0;
			if (instructionCounter < regularCounter)
			    nextAddress = (instructionCounter - regularCounter) + 1 ;
			else
			    nextAddress = (regularCounter- instructionCounter) - 1;
            
			resolved.get(instructionCounter).immediate = nextAddress;
		    }
	    }
        return resolved;
    }

}