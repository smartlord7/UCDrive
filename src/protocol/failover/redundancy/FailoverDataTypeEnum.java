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

package protocol.failover.redundancy;

import java.io.Serializable;

/**
 * This enum is used to represent the data type of the failover data.
 * FILE - Failover data about a a file.
 * DB_DML - Failover data about database DML.
 */

public enum FailoverDataTypeEnum implements Serializable {
    FILE, DB_DML
}
