import com.fourspaces.couchdb.Session;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by msveshnikov on 24.09.2014.
 */
public class Test {
    private Session dbSession;

    public static void main(String [] args)
    {
        new Test().connectCouch();
    }

    private void connectCouch() {
        String json = "{\"total_rows\":7,\"offset\":0,\"rows\":[\n" +
                "{\"id\":\"e642812a8152d2c3321cb3438e0023d5\",\"key\":335,\"value\":null}\n" +
                "]}";
        JSONObject jsonObject = JSONObject.fromObject(json);
        int i=jsonObject.getJSONArray("rows").getJSONObject(0).getInt("key");
        System.out.println(i);

        dbSession = new Session("localhost", 5984);
        String dbname = "shortener";

        List<String> listofdb = dbSession.getDatabaseNames();
        if (!listofdb.contains(dbname))
            dbSession.createDatabase(dbname);
    }

}
