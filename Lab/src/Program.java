import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Program{
    public static void main(String[] args){
        Map<Character,String> map=new HashMap<>();
        map.put('<',"RELOP, LT");
        map.put('>',"RELOP, GT");
        map.put('+',"RELOP, ADD");
        map.put('=',"ASSIGN_OP");
        map.put('(',"BRACKET, LRO");
        map.put(')',"BRACKET, RRO");
        map.put('[',"BRACKET, LSQ");
        map.put(']',"BRACKET, RSQ");
        map.put('{',"BRACKET, LCU");
        map.put('}',"BRACKET, RCU");
        map.put('.',"DOT");
        map.put(';',"SEMICOLON");

        Map<String, String> IDmap=new HashMap<>();
        IDmap.put("public","PUBLIC");
        IDmap.put("class","CLASS");
        IDmap.put("static","STATIC");
        IDmap.put("void","VOID");
        IDmap.put("String","STRING");
        IDmap.put("int","INT");

        try{
            BufferedReader br=new BufferedReader(new FileReader("resource/input.txt"));
            File file=new File("resource/output.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fw=new FileWriter(file);
            String content;
            char[] chars;
            int beg,end;
            int state;
            //读取input文件内容
            while((content=br.readLine())!=null){
                chars=content.toCharArray();
                beg=0;
                end=0;
                state=1;
                while(end<content.length()){
                    switch(state){
                        case 1:
                            if(chars[end]==' '){
                                beg++;
                                end++;
                                break;
                            }else if((chars[end]>='a' && chars[end]<='z') || (chars[end]>='A' && chars[end]<='Z')){
                                state=6;
                                end++;
                                break;
                            }else if(chars[end]>='0' && chars[end]<='9'){
                                state=7;
                                end++;
                                break;
                            }else if(chars[end]=='_'){
                                state=2;
                                end++;
                                break;
                            }else if(chars[end]=='"'){
                                state=3;
                                beg++;
                                end++;
                                break;
                            }else if(chars[end]=='<' || chars[end]=='>' || chars[end]=='=' || chars[end]=='(' || chars[end]==')' || chars[end]=='[' || chars[end]==']' || chars[end]=='{' || chars[end]=='}' || chars[end]=='.' || chars[end]==';' || chars[end]=='+'){
                                state=5;
                                break;
                            }else{
                                throw new NoTokenException(content.substring(beg,end));
                            }
                        case 2:
                            if((chars[end]>='a' && chars[end]<='z') || (chars[end]>='A' && chars[end]<='Z')){
                                state=6;
                                end++;
                            }else{
                                throw new NoTokenException(content.substring(beg,end));
                            }
                            break;
                        case 3:
                            if(chars[end]=='"'){
                                state=5;
                            }else{
                                end++;
                            }
                            break;
                        case 4:
                            if(chars[end]>='0' && chars[end]<='9'){
                                state=8;
                                end++;
                            }else{
                                throw new NoTokenException(content.substring(beg,end));
                            }
                            break;
                        case 5:
                            if(chars[end]=='"'){
                                //如果以"结束，则代表是两个"之间的内容
                                fw.write("<LITERAL, "+content.substring(beg,end)+">\n");
                                end++;
                                beg=end;
                                state=1;
                            }else if(map.containsKey(chars[end])){
                                //不是"结束但是规定的符号
                                fw.write("<"+map.get(chars[end])+">\n");
                                end++;
                                beg=end;
                                state=1;
                            }else{
                                throw new NoTokenException(content.substring(beg,end));
                            }
                            break;
                        case 6:
                            if((chars[end]>='a' && chars[end]<='z') || (chars[end]>='A' && chars[end]<='Z') || (chars[end]>='0' && chars[end]<='9')){
                                end++;
                            }else{
                                //首先判断是不是关键字
                                if(IDmap.containsKey(content.substring(beg,end))){
                                    //是关键字，查找关键字表
                                    fw.write("<"+IDmap.get(content.substring(beg,end))+">\n");
                                }else{
                                    //不是关键字，判断为标识符
                                    fw.write("<ID, "+content.substring(beg,end)+">\n");
                                }
                                beg=end;
                                state=1;
                            }
                            break;
                        case 7:
                            if(chars[end]=='.'){
                                //数字中出现. ，为小数
                                state=4;
                                end++;
                            }else if(chars[end]>='0' && chars[end]<='9'){
                                end++;
                            }else{
                                //输出数字
                                fw.write("<NUM, "+content.substring(beg,end)+">\n");
                                beg=end;
                                state=1;
                            }
                            break;
                        case 8:
                            if(chars[end]>='0' && chars[end]<='9'){
                                //判断下一个字符是否为数字，若是，继续下一个字符
                                end++;
                            }else {
                                //已经结束，是小数数字
                                fw.write("<NUM, " + content.substring(beg, end) + ">\n");
                                beg = end;
                                state = 1;
                            }
                            break;
                    }
                }
            }
            fw.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoTokenException e) {
            e.printStackTrace();
        }
    }
}

class NoTokenException extends Exception{
    String Errorstr;
    public NoTokenException(String s){
        Errorstr=s;
    }
    @Override
    public void printStackTrace() {
        super.printStackTrace();
        System.out.print("出现错误，无法识别为TOKEN，错误字符串为 "+Errorstr);
    }
}