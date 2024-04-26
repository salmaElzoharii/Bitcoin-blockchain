

import java.util.ArrayList;

import classes.Transaction.Input;

public class TxHandler {

    private UTXOPool utxoPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. 
     */
    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
      
    }
    public UTXOPool getUTXOPool() {
     return this.utxoPool;
    }
    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
    
    	    ArrayList<Transaction.Input> inputs = tx.getInputs();
    	    ArrayList<Transaction.Output> outputs = tx.getOutputs();
    	    ArrayList<UTXO> spentOutputs = new ArrayList<>();
    	    int counter = 0, opSum = 0, ipSum = 0;
    	    for (Transaction.Input input : inputs) {
    	        UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
    	        Transaction.Output prevOutput = utxoPool.getTxOutput(utxo);
    	        for(UTXO u : spentOutputs)
    	        {
    	        	 if (spentOutputs.contains(u))
    	    	        {
    	    	        	return false;
    	    	        }
    	        }
    	       
    	       
        
    	        if (utxoPool.contains(utxo) && Crypto.verifySignature(prevOutput.address, tx.getRawDataToSign(counter), input.signature)) {
    	        	spentOutputs.add(utxo);
    	        	ipSum += prevOutput.value;
    	        } else {
    	            return false;
    	        }
    	        counter++;
    	    }
    	    for (Transaction.Output output : outputs) {
    	        if (output.value < 0) {
    	            return false;
    	        }
    	        opSum += output.value;
    	    }
    	    if (!(ipSum >= opSum)) {
    	        return false;
    	    }
    	    return true;
    	
    	
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
    	ArrayList<Transaction>validTxs=new ArrayList<Transaction>();
    		for (Transaction transaction :possibleTxs )
        	{
        		if(isValidTx(transaction))
        		{
        			validTxs.add(transaction);
        			for (Transaction.Input input : transaction.getInputs())
        	    	{
        			UTXO utxo=new UTXO(input.prevTxHash,input.outputIndex);
        			utxoPool.removeUTXO(utxo);
        		}
        			int index=0;
        			for(Transaction.Output output:transaction.getOutputs())
        			{
        				UTXO utxo=new UTXO(transaction.getHash(),index);
        				index++;
        				utxoPool.addUTXO(utxo, output);
        			}
        			
        			}
        	}
    		
    		return validTxs.toArray(new Transaction[0]);
    	
    	
    }
//    I acknowledge that I am aware of the academic integrity guidelines of this course,
//    and that I worked on this assignment independently without any unauthorized help with coding or testing.” salma

}
