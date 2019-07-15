package projectTest;

import java.io.*;
import java.net.*;
import java.util.zip.*;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.*;

public class Main{
    public static void main(String []args){
        try{
            download_jar(args[0]);

            PrintWriter output = new PrintWriter("Output.txt", "UTF-8");

            // Reading jar file to get desired result
            FileInputStream f = new FileInputStream("aelfred-1.2.jar");
            ZipInputStream zip_input= new ZipInputStream(new BufferedInputStream(f));
            ZipEntry entry;
            HashMap<String, Integer> map = new HashMap<String,Integer>();
            List<Integer> pool_size = new ArrayList<Integer>();
            List<Integer> number_methods = new ArrayList<Integer>();
            while((entry = zip_input.getNextEntry())!= null){
                String name= new String(entry.getName());         // stores the name of the class file
                if((name.length()-5)>=0){
                    String cm = new String(name.substring(name.length()-5));
                    if(cm.equals("class")){
                        //System.out.println(name);
                        String ss= new String(name.substring(0,name.length()-6));
                        //System.out.println(ss);
                        List<String> cmdList = new ArrayList<String>(); //using javap to extract information from class
                        cmdList.add("javap");
                        cmdList.add("-verbose");
                        cmdList.add("-classpath");
                        cmdList.add("aelfred-1.2.jar");
                        cmdList.add(ss);

                        // Constructing ProcessBuilder with List as argument
                        ProcessBuilder pb = new ProcessBuilder(cmdList);

                        Process p = pb.start();

                        InputStream is = p.getInputStream();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        String line;
                        int countnum=0;
                        while ((line = reader.readLine()) != null) {
                            String pattern = "^[ \t]*#[0-9]*";
                            String pattern2 ="[0-9]+:[ \t]+.*";
                            String pattern3 = "methods:[ \t]*[0-9]*";
                            // Creating  Pattern object to match regular expressions
                            Pattern r = Pattern.compile(pattern);
                            Pattern r2= Pattern.compile(pattern2);
                            Pattern r3= Pattern.compile(pattern3);
                            // Creating  matcher object.
                            Matcher m = r.matcher(line);
                            Matcher m2 = r2.matcher(line);
                            Matcher m3= r3.matcher(line);
                            if (m.find( )) {
                                countnum++;
                            }
                            if(m2.find()){
                                String match = new String(m2.group(0));
                                //System.out.println(match);
                                String[] array = match.split(" ");
                                //System.out.println(array[1]);
                                if(map.containsKey(array[1])){
                                    map.put(array[1], map.get(array[1]) + 1);
                                }
                                else{
                                    map.put(array[1],1);
                                }
                            }
                            if(m3.find()){
                                String matched = new String(m3.group(0));

                                int num_methods = Integer.parseInt(matched.substring(9));
                                //output.println(num_methods);
                                number_methods.add(num_methods);
                            }
                        }
                        //System.out.println(countnum);
                        pool_size.add(countnum);
                        p.waitFor();
                        reader.close();
                    }
                }
            }
            double average,minimum,maximum,std_dev=0,avg_methods=0;
            int sum=0;
            minimum= pool_size.get(0);
            maximum = pool_size.get(0);
            for(int i=0;i<pool_size.size();i++){                       // computing average, min, max, std_deviation for pool sizes
                sum= sum+ pool_size.get(i);
                if(pool_size.get(i)<minimum){
                    minimum= pool_size.get(i);
                }
                if(pool_size.get(i)>maximum){
                    maximum= pool_size.get(i);
                }
                //System.out.println(pool_size.get(i));
            }
            average= (double)sum/pool_size.size();
            for(int i=0;i<pool_size.size();i++){
                std_dev = std_dev + ((pool_size.get(i)-average)*(pool_size.get(i)-average));
            }
            std_dev= std_dev/pool_size.size();
            std_dev = Math.sqrt(std_dev);
            output.println("The average, maximum, minimum and standard deviation of constant pool size of classes in jar file: ");
            output.println("Average: " + average);
            output.println("Maximum: " + maximum);
            output.println("Minimum: " + minimum);
            output.println("Standard Deviation: " + std_dev);
            output.println("");
            output.println("");
            output.println("Top 50 JVM instructions(in terms of occurence frequency) across all classes: ");
            for(int i=0;i<50;i++){
                int maxvalue=0;
                String maxkey ="Empty";
                Iterator<HashMap.Entry<String, Integer>> itr = map.entrySet().iterator();
                while(itr.hasNext())
                {
                    HashMap.Entry<String, Integer> e = itr.next();
                    if(e.getValue()>maxvalue){
                        maxvalue= e.getValue();
                        maxkey = e.getKey();
                    }
                    //System.out.println("Key = " + e.getKey() + ", Value = " + e.getValue());
                }
                output.println("Instruction: " + maxkey + ", Occurences = " + maxvalue);
                map.remove(maxkey);
            }

            for(int i=0;i<number_methods.size();i++){
                avg_methods= avg_methods + number_methods.get(i);
            }
            avg_methods= avg_methods/number_methods.size();
            output.println("");
            output.println("");
            output.println("Average number of methods in a class: " + avg_methods);
            output.close();
            zip_input.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
    }

    private static void download_jar(String arg) {
        // Downloading jar file
        URL url = new URL(arg);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fos = new FileOutputStream("aelfred-1.2.jar");
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fos.write(buffer, 0, count);
        }
        fos.close();
        bis.close();
    }

    public void helloWorld(){
        System.out.println("Hello world");
    }
}