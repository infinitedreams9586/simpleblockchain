import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class NoobChain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 3;

    public static void main(String[] args){

        Block genesisBlock = new Block("I am the first block", "0");
        blockchain.add(genesisBlock);
        blockchain.get(0).mineBlock(difficulty);

        Block secondBlock = new Block("I am the second block", genesisBlock.hash);
        blockchain.add(secondBlock);
        blockchain.get(1).mineBlock(difficulty);

        Block thirdBlock = new Block("I am the third block", secondBlock.hash);
        blockchain.add(thirdBlock);
        blockchain.get(2).mineBlock(difficulty);


        System.out.println("Blockchain is valid : " + isChainValid());

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);
    }
    
    public static Boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);

            if(!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("Current hashes are not equal");
                return false;
            }

            if(!previousBlock.hash.equals(currentBlock.previousHash)){
                System.out.println("Previous hashes are not equal");
                return false;
            }
        }
        return true;
    }
}
