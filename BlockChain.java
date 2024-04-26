// The BlockChain class should maintain only limited block nodes to satisfy the functionality.
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.

import java.util.ArrayList;
import java.util.HashMap;

 public class BlockChain {
     public static final int CUT_OFF_AGE = 10;
     class Node {
         Block block;
         int height;
         UTXOPool node_pool;

         Node(Block block, int height, UTXOPool node_pool) {
             this.block = block;
             this.height = height;
             this.node_pool = new UTXOPool(node_pool);
         }
     }  
     UTXOPool utxoPool;
     TransactionPool TXsPool;
     Node maxNode;
//     ArrayList<Node> chain;
     HashMap<byte[], Node> Longestchain;
     public BlockChain(Block genesisBlock) {
     	TXsPool = new TransactionPool();
     	utxoPool=new UTXOPool();
     	Transaction coinBasetx=genesisBlock.getCoinbase();
     	
     	 int i=0;
     	 for (Transaction.Output out: coinBasetx.getOutputs())
     	 {
     		   UTXO utxo = new UTXO(coinBasetx.getHash(), i++);
                utxoPool.addUTXO(utxo, out);
     	 }
     	 Node genesis_node=new Node(genesisBlock,0,utxoPool);
     	 maxNode=genesis_node;
     	 Longestchain = new HashMap<>();
     	 TXsPool.addTransaction(coinBasetx);
     	 Longestchain.put(genesisBlock.getHash(), genesis_node);
     }

     /** Get the maximum height block */
     public Block getMaxHeightBlock() {
     	return maxNode.block;
     }

     /** Get the UTXOPool for mining a new block on top of max height block */
     public UTXOPool getMaxHeightUTXOPool() {
         // IMPLEMENT THIS
     	return maxNode.node_pool;
     }

     /** Get the transaction pool to mine a new block */
     public TransactionPool getTransactionPool() {
         // IMPLEMENT THIS
     	return TXsPool;
     }

     /**
      * Add {@code block} to the blockchain if it is valid. For validity, all transactions should be
      * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}, where maxHeight is 
      * the current height of the blockchain.
 	 * <p>
 	 * Assume the Genesis block is at height 1.
      * For example, you can try creating a new block over the genesis block (i.e. create a block at 
 	 * height 2) if the current blockchain height is less than or equal to CUT_OFF_AGE + 1. As soon as
 	 * the current blockchain height exceeds CUT_OFF_AGE + 1, you cannot create a new block at height 2.
      * 
      * @return true if block is successfully added
      */
     public boolean addBlock(Block block) {
  
     	if(block.getPrevBlockHash()==null)
     		{return false;}
     	Node prev_block=Longestchain.get(block.getPrevBlockHash());
      TxHandler handler=new TxHandler(prev_block.node_pool);
     	if (prev_block.height < maxNode.height - CUT_OFF_AGE) //previous block is old,don't add
     	{return false;}
     	Transaction[] validTxs = handler.handleTxs(block.getTransactions().toArray(new Transaction[0]));
         if (validTxs.length < block.getTransactions().size()) 
         { return false;}
         UTXOPool modified_Pool = handler.getUTXOPool();
         int i=0;
    	 for (Transaction.Output out: block.getCoinbase().getOutputs())
    	 {
    		   UTXO utxo = new UTXO(block.getCoinbase().getHash(), i++);
    		   modified_Pool.addUTXO(utxo, out);
    	 }
addTransaction(block.getCoinbase());
  
         Node Node = new Node(block,prev_block.height + 1, modified_Pool);
         Longestchain.put(block.getHash(), Node);
         maxNode = Node;
         return true;
     }

     /** Add a transaction to the transaction pool */
     public void addTransaction(Transaction tx) {
     	TXsPool.addTransaction(tx);   		
     }
 //  I acknowledge that I am aware of the academic integrity guidelines of this course,
 //  and that I worked on this assignment independently without any unauthorized help with coding or testing.” salma
 }
