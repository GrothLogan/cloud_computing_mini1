import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


// use along with hadoop5 to get answer
public class access_log_hadoop9 {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, DoubleWritable>{

    private final static DoubleWritable one = new DoubleWritable(1);
    private Text word = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      
      //String logEntryPattern = "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+) \"([^\"]*)\" \"([^\"]*)\"";
      //swap the d+ to S+ ssince it coul be -
      //String logEntryPattern = "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\S+) \"([^\"]*)\" \"([^\"]*)\"";
      //String logEntryPattern = "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+|-)$";
      String logEntryPattern = "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+|-) *";
      // now it just ignores everything after bits
      // the match is not working here for some reason
      Pattern pattern = Pattern.compile(logEntryPattern);
      String value2 = value.toString();
      
      Matcher matcher = pattern.matcher(value2);
        if(matcher.find())
        {
          String ipAddress = matcher.group(1);
          String userIdentifier = matcher.group(2);
          String userId = matcher.group(3);
          String timestamp = matcher.group(4);
          String request = matcher.group(5);
          String statusCode = matcher.group(6);
          String bytesSent = matcher.group(7);
          String[] stuff = request.split(" ");
          // stuff 0 should be method 1 should be path 2 query/protocol/request
          //String referer = matcher.group(8);
          //String userAgent = matcher.group(9);
          if(!bytesSent.equals("-"))
          {
            DoubleWritable bytes = new DoubleWritable(Double.parseDouble(bytesSent));
            word.set(ipAddress);
            context.write(word, bytes );
          }

          
        }
        else{
          System.out.println("somethings wrong with this line");
          System.out.println(value2);
        }
      

  }
  }

  public static class IntSumReducer
       extends Reducer<Text,DoubleWritable,Text,DoubleWritable> {
    private DoubleWritable result = new DoubleWritable();

    public void reduce(Text key, Iterable<DoubleWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      for (DoubleWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "access_log_hadoop9");
    job.setJarByClass(access_log_hadoop9.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(DoubleWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
