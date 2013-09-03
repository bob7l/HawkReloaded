package uk.co.oliwali.HawkEye.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class DeleteEntry implements Runnable {

	private final List<Integer> ids = new ArrayList<Integer>();

	public DeleteEntry(Integer id) {
		ids.add(id);
	}
	public DeleteEntry(DataEntry entry) {
		ids.add(entry.getDataId());
	}
	public DeleteEntry(List<?> entries) {
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i) instanceof DataEntry)
				ids.add(((DataEntry)(entries.get(i))).getDataId());
			else ids.add((Integer)entries.get(i));
		}
	}

	public void run() {
		JDCConnection conn = null;
		PreparedStatement stmnt = null;
		try {
			conn = DataManager.getConnection();
			
			conn.setAutoCommit(false); 
			
			stmnt = conn.prepareStatement("DELETE FROM `" + Config.DbHawkEyeTable + "` WHERE `data_id` = ?");
			
			int i = 0;
			
			for (Integer id : ids) {
				stmnt.setInt(1, id);
				stmnt.addBatch();
                i++;
				if (i % 1000 == 0) stmnt.executeBatch(); //If the batchsize is divisible by 1000, execute!
			}
			
			stmnt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException ex) {
			Util.severe("Unable to delete data entries from MySQL database: " + ex);
		} finally {
			conn.close();
		}
	}

}