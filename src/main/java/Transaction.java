import java.security.*;
import java.util.ArrayList;

public class Transaction {
    public String transactionId;
    public PublicKey sender;
    public PublicKey receipient;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
        this.sender = from;
        this.receipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash(){
        sequence++;
        return StringUtility.applySHA256(StringUtility.getStringFromKey(sender)
            + StringUtility.getStringFromKey(receipient)
            + Float.toString(value)
            + sequence);
    }

    public void generateSignature(PrivateKey privateKey){
        String data = StringUtility.getStringFromKey(sender) +
                StringUtility.getStringFromKey(receipient) +
                Float.toString(value);
        signature = StringUtility.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature(){
        String data = StringUtility.getStringFromKey(sender) +
                StringUtility.getStringFromKey(receipient) +
                Float.toString(value);
        return StringUtility.verifyECDSASig(sender, data, signature);
    }

    public float getInputsValue(){
        float total = 0;
        for (TransactionInput i : inputs){
            if(i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue(){
        float total = 0;
        for (TransactionOutput o : outputs){
            total += o.value;
        }
        return total;
    }

    public boolean processTransaction(){
        if (verifySignature() == false){
            StringUtility.Print("Transaction signature verification failed");
            return false;
        }

        for (TransactionInput i : inputs){
            i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId);
        }

        if (getInputsValue() < NoobChain.minimumTransaction){
            StringUtility.Print("Transaction input is too small");
        }

        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.receipient, value, transactionId));
        outputs.add(new TransactionOutput(this.sender, value, transactionId));

        for (TransactionOutput o:outputs){
            NoobChain.UTXOs.put(o.id, o);
        }

        for (TransactionInput i:inputs){
            if(i.UTXO == null) continue;
            NoobChain.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }
}
