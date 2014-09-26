import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by msveshnikov on 24.09.2014.
 */
public class Test {

    public static void main(String[] args) throws IOException {
        new Test().connectCouch();
    }

    private void connectCouch() throws IOException {
        String json = "{\"total_rows\":7,\"offset\":0,\"rows\":[\n" +
                "{\"id\":\"e642812a8152d2c3321cb3438e0023d5\",\"key\":335,\"value\":null}\n" +
                "]}";
        JSONObject jsonObject = JSONObject.fromObject(json);
        int i = jsonObject.getJSONArray("rows").getJSONObject(0).getInt("key");
        System.out.println(i);

        Session dbSession = new Session("localhost", 5984);
        String dbname = "shortener";

        List<String> listofdb = dbSession.getDatabaseNames();
        if (!listofdb.contains(dbname))
            dbSession.createDatabase(dbname);
        Database db = dbSession.getDatabase(dbname);
        Document no=db.getDocument("max");
        Document doc = new Document();
        doc.setId("1");
        doc.put("id", 3);
        db.saveDocument(doc);
        doc = new Document();
        doc.setId("1");
        doc.put("id", 3);
        db.saveDocument(doc);
    }

}
