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

package protocol.clientserver;

import java.io.Serializable;

/**
 * This enum is used to represent the response status with the following status:
 * ERROR - An error occurred.
 * SUCCESS - Everything executed successfully.
 * UNAUTHORIZED - Missing permissions.
 */

public enum ResponseStatusEnum implements Serializable {
    ERROR, SUCCESS, UNAUTHORIZED
}
