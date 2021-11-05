import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

class Tag{
    int index;
    int length;
    char nextChar;

    Tag(int idx,int len,char Char){
        index=idx; length=len; nextChar=Char;
    }

    @Override
    public String toString() {
        return "<"+index+","+length+","+nextChar+">";
    }

}

public class lz77 {
    private static final int defaultBuffSize = 64;
    private int buffSize;
    private StringBuffer searchBuffer;
    private Reader R;
    private PrintWriter W;
    ArrayList<Tag> tags=new ArrayList<>();


    public lz77() {
        this(defaultBuffSize);
    }
    public lz77(int bufferSize){
        buffSize=bufferSize;
        searchBuffer = new StringBuffer(buffSize);
    }
    public void trimSearchBuffer(){
        if(searchBuffer.length()>buffSize){
            searchBuffer = searchBuffer.delete(0,searchBuffer.length()-buffSize);
        }
    }

    public void compress(String In) throws IOException {
       R=new BufferedReader(new FileReader(In));
       W=new PrintWriter(new BufferedWriter(new FileWriter(In+"Compressed")));

       int nextChar; String match="";
       int matchIdx=0, tempIdx=0;

       while ((nextChar=R.read())!=-1){

           tempIdx=searchBuffer.lastIndexOf(match+(char)nextChar);
           if(tempIdx!=-1){
               match+=(char)nextChar;
               matchIdx=tempIdx;
           }else{
               int offset=searchBuffer.length()-matchIdx;
                if(match.equals(""))offset=0;
               Tag tag = new Tag(offset,match.length(),(char)nextChar);
               tags.add(tag);

               searchBuffer.append(match+(char)nextChar);
               match="";
               matchIdx=0;
               trimSearchBuffer();
           }

       }
        if(!match.equals("")){
            nextChar=(int)match.charAt(match.length()-1);
            match=match.substring(0,match.length()-1);
            tempIdx=searchBuffer.lastIndexOf(match);
            int offset=searchBuffer.length()-tempIdx;
            Tag tag=new Tag(offset,match.length(),(char)nextChar);
            tags.add(tag);
            searchBuffer.append(match+(char)nextChar);
            match="";
            matchIdx=0;
            trimSearchBuffer();
        }
        for (int i = 0; i < tags.size(); i++) {
            W.println(tags.get(i));
        }
       R.close();
       W.flush();
       W.close();

    }

    public void decompress(String In) throws IOException{
        R=new BufferedReader(new FileReader(In+"Compressed"));
        W=new PrintWriter(new BufferedWriter(new FileWriter(In+"decompressed")));
        searchBuffer.setLength(0);
        int nextChar,offset,length,index;
        StreamTokenizer st = new StreamTokenizer(R);

        st.ordinaryChar((int)' ');
        st.ordinaryChar((int)'.');
        st.ordinaryChar((int)',');
        st.ordinaryChar((int)'<');
        st.ordinaryChar((int)'>');
        st.whitespaceChars((int)',',(int)',');
        st.wordChars((int)'\n', (int)'\n');

        while (st.nextToken()!=StreamTokenizer.TT_EOF){
            switch (st.ttype){
                case StreamTokenizer.TT_NUMBER:
                    index=(int)st.nval;
                    if(index>0) {
                        offset = searchBuffer.length() - index;
                    }
                    else{
                        offset=index;
                    }
                    st.nextToken();
                    if(st.ttype==StreamTokenizer.TT_WORD){ //then first was word
                        searchBuffer.append(st.sval);
                        W.print(st.sval);
                        break;
                    }
                    length= (int) st.nval;

                    String output = searchBuffer.substring(offset,offset+length);
                    searchBuffer.append(output);
                    W.print(output);
                    trimSearchBuffer();
                    break;
                case StreamTokenizer.TT_WORD:
                    searchBuffer.append(st.sval);
                    W.print(st.sval);
                    trimSearchBuffer();

                default:
            }
        }

        R.close();
        W.flush();
        W.close();
    }
    public static void main(String[] args) {
        String output= "1.Compress\n2.decompress";
        System.out.println(output);
        Scanner input = new Scanner(System.in);

        int a = input.nextInt();


        String b = "E:\\programming\\Lz77\\src\\input";
        lz77 lz77 = new lz77(1024);
        try {
            switch (a){
                case 1:
                    lz77.compress(b);
                    break;
                case 2:
                    lz77.decompress(b);
                    break;
                default:
            }
        } catch (FileNotFoundException f) {
        System.err.println("File not found: "+b);
    } catch (IOException e) {
        System.err.println("Problem processing file: "+b);
    }
    }
}


