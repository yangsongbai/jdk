import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

public class Test {
    public static void main(String[] args) {

        String pass = getRandomString(10);
        pass+="cloudstorage";
        System.out.println(pass);
        //编码
        String encode = Base64.getEncoder().encodeToString(pass.getBytes(StandardCharsets.UTF_8));
        System.out.println(encode);

        //解码
        String decode = new String(Base64.getDecoder().decode(encode), StandardCharsets.UTF_8);
        System.out.println(decode);
    }

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
