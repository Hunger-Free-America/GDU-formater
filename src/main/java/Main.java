import java.io.*;
import java.util.*;

public class Main {

    public static final String SERVICE_FILE_PATH = "/home/atticus/dev/HFA/HoursFlatener/src/main/resources/Services_Export_21-06-04.csv";
    public static final String HOURS_FILE_PATH = "/home/atticus/dev/HFA/HoursFlatener/src/main/resources/Hours_Without_Details_21-06-04.csv";
    public static final String FILE_DELIMINATOR = ",";
    public static final String HEADER = "Id,Accepts_food_donations__c,Phone,Ext,Account__c,Address__r.npsp__MailingStreet__c,Address__r.npsp__MailingStreet2__c,Address__r.npsp__MailingCity__c,Address__r.npsp__MailingState__c,Address__r.npsp__MailingPostalCode__c,Alternate_Name__c,Application_Process__c,by_appointment_only__c,Description__c,Email__c,Location_Latitude__c,Location_Longitude__c,Name,Primary_Phone_Number__c,Primary_Phone__c,Private__c,Reason_for_Temporary_Closure__c,Service_18_ID__c,Service_Area__c,Service_at_Location_ID__c,Status__c,Type__c,Website__c,Mon,Tues,Wed,Thurs,Fri,Sat,Sun";


    public static void main(String [] args) throws Exception{
        InputStream inputStream = new FileInputStream(SERVICE_FILE_PATH);
        List<List<String>> services = readCSV(inputStream);
        inputStream = new FileInputStream(HOURS_FILE_PATH);
        List<List<String>> hours = readCSV(inputStream);
        inputStream.close();

        //Sort the hours file by service id and day of the week
        Comparator<List<String>> comparator = createComparator(0,5);
        hours.sort(comparator);

        //TODO: add a function to format the file in the google GDU template
        //Concatenate open and close hours and add them to the services file
        for(List<String> service : services){
            String serviceID = service.get(0);
            String mon,tues,wed,thurs,fri,sat,sun;
            mon = tues = wed = thurs = fri = sat = sun = "";
            for(List<String> hour : hours){
                if(serviceID.equals(hour.get(0))){
                    //looks pretty but I don't think its the most efficient way to do this :/ EDIT: STILL ONLY TOOK 5 SECONDS FOR A 40K LINE FILE THO!!!
                    switch(Integer.valueOf(hour.get(5))) {
                        case 1:
                            if(mon.length() == 0){
                                mon = hour.get(3) + " - " + hour.get(4);
                            } else { mon += ";" + hour.get(3) + " - " + hour.get(4);}
                            break;
                        case 2:
                            if(tues.length() == 0){
                                tues = hour.get(3) + " - " + hour.get(4);
                            } else { tues += ";" + hour.get(3) + " - " + hour.get(4);}
                            break;
                        case 3:
                            if(wed.length() == 0){
                                wed = hour.get(3) + " - " + hour.get(4);
                            } else { wed += ";" + hour.get(4) + " - " + hour.get(4);}
                            break;
                        case 4:
                            if(thurs.length() == 0){
                                thurs = hour.get(3) + " - " + hour.get(4);
                            } else { thurs += ";" + hour.get(3) + " - " + hour.get(4);}
                            break;
                        case 5:
                            if(fri.length() == 0){
                                fri = hour.get(3) + " - " + hour.get(4);
                            } else { fri += ";" + hour.get(3) + " - " + hour.get(4);}
                            break;
                        case 6:
                            if(sat.length() == 0){
                                sat = hour.get(3) + " - " + hour.get(4);
                            } else { sat += ";" + hour.get(3) + " - " + hour.get(4);}
                            break;
                        case 7:
                            if(sun.length() == 0){
                                sun = hour.get(4) + " - " + hour.get(4);
                            } else { sun += ";" + hour.get(3) + " - " + hour.get(4);}
                            break;
                    }
                }
            }
            service.add(mon);
            service.add(tues);
            service.add(wed);
            service.add(thurs);
            service.add(fri);
            service.add(sat);
            service.add(sun);
        }

        OutputStream  outputStream = new FileOutputStream("service_and_hours.csv");
        WriteCSV(HEADER, services, outputStream);
    }

    private static void WriteCSV(String header, List<List<String>> lines, OutputStream outputStream) throws IOException{
        Writer writer = new OutputStreamWriter(outputStream);
        writer.write(HEADER);
        for(List<String> line : lines){
            for(int i = 0; i < line.size(); i++){
                writer.write(line.get(i));
                if(i < line.size()-1){
                    writer.write(FILE_DELIMINATOR);
                }
            }
            writer.write("\n");
        }
        writer.close();
    }

    private static List<List<String>> readCSV(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<List<String>> lines = new ArrayList<List<String>>();
        String line = reader.readLine(); //Skips header

        while(true){
            line = reader.readLine();
            if(line == null) break; //exit the loop at the end of the file

            List<String> list = new ArrayList(Arrays.asList(line.split(FILE_DELIMINATOR)));
            lines.add(list);
        }
        return lines;
    }

    public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
        return new Comparator<T>(){
            @Override
            public int compare(T t0, T t1){
                return t0.compareTo(t1);
            }
        };
    }


     private static <T extends Comparable<? super T>> Comparator<List<T>> createComparator(int... indices) {
        return createComparator(Main.<T>naturalOrder(), indices);
    }

    private static <T> Comparator<List<T>> createComparator(final Comparator<? super T> delegate, final int... indices) {
        return new Comparator<List<T>>() {
            @Override
            public int compare(List<T> list0, List<T> list1) {
                for (int i = 0; i < indices.length; i++) {
                    T element0 = list0.get(indices[i]);
                    T element1 = list1.get(indices[i]);
                    int n = delegate.compare(element0, element1);
                    if (n != 0)
                    {
                        return n;
                    }
                }
                return 0;
            }
        };
    }
}
