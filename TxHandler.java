import java.util.ArrayList;

public class TxHandler
{

	public UTXOPool	utxoPool;

	/**
	 * Creates a public ledger whose current UTXOPool (collection of unspent
	 * transaction outputs) is {@code utxoPool}. This should make a copy of
	 * utxoPool by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public TxHandler(UTXOPool utxoPool)
	{
		// IMPLEMENT THIS
		this.utxoPool = new UTXOPool(utxoPool);
	}

	private void updateUtxoPool(Transaction tx)
	{
		ArrayList<Transaction.Input> txInputs = tx.getInputs();
		for (Transaction.Input txIn : txInputs)
		{
			UTXO utxo = new UTXO(txIn.prevTxHash, txIn.outputIndex);
			utxoPool.removeUTXO(utxo);
		}

		for (int index = 0; index < tx.numOutputs(); ++index)
		{
			Transaction.Output txOut = tx.getOutput(index);
			UTXO utxo = new UTXO(tx.getHash(), index);
			utxoPool.addUTXO(utxo, txOut);
		}
	}

	/**
	 * @return true if: (1) all outputs claimed by {@code tx} are in the current
	 *         UTXO pool, (2) the signatures on each input of {@code tx} are
	 *         valid, (3) no UTXO is claimed multiple times by {@code tx}, (4)
	 *         all of {@code tx}s output values are non-negative, and (5) the
	 *         sum of {@code tx}s input values is greater than or equal to the
	 *         sum of its output values; and false otherwise.
	 */
	public boolean isValidTx(Transaction tx)
	{
		// IMPLEMENT THIS
		if (null == tx) return true;

		double inputTxSum = 0;
		double outputTxSum = 0;

		ArrayList<Transaction.Input> txInputs = tx.getInputs();
		ArrayList<Transaction.Output> txOutputs = tx.getOutputs();

		// (1) all outputs claimed by {@code tx} are in the current UTXO pool
		for (Transaction.Input input : txInputs)
		{
			UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
			if (!utxoPool.contains(utxo)) return false;
		}

		// (2) the signatures on each input of {@code tx} are valid
		for (int index = 0; index < tx.numInputs(); index++)
		{
			Transaction.Input input = tx.getInput(index);
			UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
			// Transaction.Output output = txOutputs.get(input.outputIndex);
			Transaction.Output output = utxoPool.getTxOutput(utxo);
			byte[] message = tx.getRawDataToSign(index);
			if (!Crypto.verifySignature(output.address, message,
					input.signature)) return false;
		}

		// (3) no UTXO is claimed multiple times by {@code tx}
		UTXOPool uniqueUTXOPool = new UTXOPool();
		for (Transaction.Input input : txInputs)
		{
			UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
			Transaction.Output output = utxoPool.getTxOutput(utxo);

			if (uniqueUTXOPool.contains(utxo)) return false;
			if (output.value < 0) return false; 

			uniqueUTXOPool.addUTXO(utxo, output);
			inputTxSum += output.value;
		}

		// (4) all of {@code tx}s output values are non-negative
		for (Transaction.Output output : txOutputs)
		{
			if (output.value < 0) return false;

			outputTxSum += output.value;
		}

		// (5) the sum of {@code tx}s input values is greater than or equal to
		// the sum of its output values, and false otherwise.
		return  (inputTxSum >= outputTxSum);
	}

	/**
	 * Handles each epoch by receiving an unordered array of proposed
	 * transactions, checking each transaction for correctness, returning a
	 * mutually valid array of accepted transactions, and updating the current
	 * UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs)
	{
		// IMPLEMENT THIS
		ArrayList<Transaction> validTxArray = new ArrayList<Transaction>();

		for (Transaction tx : possibleTxs)
		{
			if (isValidTx(tx))
			{
				validTxArray.add(tx);
				updateUtxoPool(tx);
			}
		}

		Transaction[] transactions = new Transaction[validTxArray.size()];
		return validTxArray.toArray(transactions);
	}
}
