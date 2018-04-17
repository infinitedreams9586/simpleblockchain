import java.util.ArrayList;
import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private long timeStamp;
    private int nonce;

    public Block(String previousHash){
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash(){
        String calculatedhash = StringUtility.applySHA256(this.previousHash + Long.toString(this.timeStamp) + Integer.toString(nonce) + merkleRoot);
        return calculatedhash;
    }

    public void mineBlock(int difficulty){
        merkleRoot = StringUtility.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)){
            nonce ++ ;
            hash = calculateHash();
        }
        StringUtility.Print("Block mined :" + hash);
    }

    public boolean addTransaction(Transaction transaction){
        if (transaction == null) return false;
        if((previousHash != "0")) {
            if((transaction.processTransaction() != true)){
                StringUtility.Print("Transaction failed to proceed.");
                return false;
            }
        }
        transactions.add(transaction);
        StringUtility.Print("Transaction added to Block");
        return true;
    }
}
