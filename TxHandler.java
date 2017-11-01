import java.util.ArrayList;

public class TxHandler {

	public UTXOPool utxoPool;
	
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
    	this.utxoPool = new UTXOPool(utxoPool);
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
        // IMPLEMENT THIS
    	boolean isValid = true;
    	int inputTxSum = 0;
    	int outputTxSum = 0;
    	    	
    	ArrayList<Transaction.Input>  txInputs  = tx.getInputs();
    	ArrayList<Transaction.Output> txOutputs = tx.getOutputs();
    	
		// (1) all outputs claimed by {@code tx} are in the current UTXO pool	
    	for (Transaction.Input input : txInputs)
    	{
    		UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
    		isValid = isValid && utxoPool.contains(utxo);  		    		    	
    	}
    	
		// (2) the signatures on each input of {@code tx} are valid
       	for (int index = 0; isValid && index < txInputs.size(); ++index)
       	{
			Transaction.Input input = tx.getInput(index);
			Transaction.Output output = txOutputs.get(input.outputIndex);
			byte[] message = tx.getRawDataToSign(index);
			isValid = isValid && Crypto.verifySignature(output.address, message, input.signature);
       	}
    	
    	// (3) no UTXO is claimed multiple times by {@code tx}
       	UTXOPool uniqueUTXOPool = new UTXOPool();
       	for (Transaction.Input input : txInputs)
       	{       		
       		UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
       		Transaction.Output output = utxoPool.getTxOutput(utxo);
       		if (isValid && (!uniqueUTXOPool.contains(utxo)))
       		{
           		uniqueUTXOPool.addUTXO(utxo, output);
           		inputTxSum += output.value;
       		}
       		else
       		{
       			isValid = false;
       			break;
       		}

       	}
       	
        // (4) all of {@code tx}s output values are non-negative
       	for (Transaction.Output output : txOutputs)
       	{
       		if (isValid && (output.value >= 0))
       		{
       			outputTxSum += output.value;
       		}
       		else
       		{
       			isValid = false;
       			break;
       		}
       		
       	}
    	
       	// (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
        //     values; and false otherwise.
    	isValid = isValid && (inputTxSum >= outputTxSum);
       	
       	return isValid;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
    	Transaction[] transaction = null;
    	
    	return transaction;
    }
}
