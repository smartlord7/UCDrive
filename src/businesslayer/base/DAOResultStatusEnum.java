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

package businesslayer.base;

import java.io.Serializable;

// region Public enum

/**
 * This enum represents the DAO results trough the following status:
 * SUCCESS - When the database has been modified successfully.
 * ERROR - When an error occurred when modifying the database.
 * IGNORED - When the modifications on the database have been ignored.
 */

public enum DAOResultStatusEnum implements Serializable {
    SUCCESS, ERROR, IGNORED
}

// endregion Public enum
