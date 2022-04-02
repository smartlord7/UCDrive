/**------------ucDrive: REPOSITÓRIO DE FICHEIROS NA UC------------
 University of Coimbra
 Degree in Computer Science and Engineering
 Sistemas Distribuidos
 3rd year, 2nd semester
 Authors:
 Sancho Amaral Simões, 2019217590, uc2019217590@student.uc.pt
 Tiago Filipe Santa Ventura, 2019243695, uc2019243695@student.uc.pt
 Coimbra, 2nd April 2022
 ---------------------------------------------------------------------------*/

package server.threads.failover;

import businesslayer.base.DAOResult;
import protocol.failover.redundancy.FailoverData;
import protocol.failover.redundancy.FailoverDataTypeEnum;
import server.struct.ServerUserSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * Class that has the failover data helper methods.
 */

public class FailoverDataHelper {

    // region Public methods

    /**
     * Method used to send the data manipulation language failover data.
     * @param session is the current server user session.
     * @param result is the DAO result status.
     * @throws IOException - whenever an input or output operation is failed or interpreted.
     */
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

    // endregion Public methods

}
