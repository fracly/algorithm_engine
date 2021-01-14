package cn.escheduler.api.utils;


import org.apache.kafka.clients.admin.*;



import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaClient implements AutoCloseable{

    public static KafkaAdminClient client ;

    public static void initializeClient(String[] hosts){
        Map<String, Object> map = new HashMap<>();
        map.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Arrays.asList(hosts));
        map.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        client = (KafkaAdminClient) KafkaAdminClient.create(map);
    }

    public static Set<String> listTopics() throws ExecutionException, InterruptedException {
        ListTopicsOptions options=new ListTopicsOptions();
        options.timeoutMs(3000);
        options.listInternal(false);
        ListTopicsResult result = client.listTopics(options);
        Set<String> topics= result.names().get();
        return topics;
    }

    public static boolean checkKafkaCluster(String hosts) throws ExecutionException, InterruptedException {
        String[] hostArray = hosts.split(",");
        initializeClient(hostArray);

        DescribeClusterResult result = client.describeCluster();
        if(result.nodes().get().size() > 0) {
            return true;
        }
        return false;
    }

    public void close() {
        if(client!=null){
            client.close();
        }

    }
}
