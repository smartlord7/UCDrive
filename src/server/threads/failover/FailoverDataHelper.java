package server.threads.failover;

import businesslayer.base.DAOResult;
import protocol.failover.redundancy.FailoverData;
import protocol.failover.redundancy.FailoverDataTypeEnum;
import server.struct.ServerUserSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class FailoverDataHelper {
    public static void sendDMLFailoverData(ServerUserSession session, DAOResult result) throws IOException {
        byte[] buf;
        FailoverData data;
        ByteArrayOutputStream byteWriter;
        ObjectOutputStream objWriter;

        byteWriter = new ByteArrayOutputStream();
        objWriter = new ObjectOutputStream(byteWriter);
        objWriter.writeObject(result);
        objWriter.flush();
        buf = byteWriter.toByteArray();

        data = new FailoverData(Arrays.hashCode(buf), buf.length, buf.length,
                result.getEntity().getClass().toString(), null, buf, FailoverDataTypeEnum.DB_DML);

        session.getDataToSync().add(data);
    }
}
