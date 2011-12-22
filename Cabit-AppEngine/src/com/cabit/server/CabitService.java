package com.cabit.server;

import java.util.List;

import com.cabit.annotation.ServiceMethod;

/***
 * 
 * @author Udi
 *	This class is the business logic (the class that will be accessible from RPC )
 * 
 */
public class CabitService {
	
	// TODO y all the methods are statics????
	
	static DataStore db = new DataStore();
    /*public static Location createLocation() {
            return db.update(new Location());
    }

    public static Location readLocation(String userEmail) {
            return db.find("");
    }

    public static Location updateLocation(Location location) {
            location.setUserEmail(DataStore.getUserEmail());
            location = db.update(location);
            //DataStore.sendC2DMUpdate(LocationChange.UPDATE + LocationChange.SEPARATOR + location.getId());
            DataStore.sendC2DMUpdate("coool" + location.getUserEmail());
            return location;

    }

    public static void deleteLocation(Location location) {

            db.delete(location.getUserEmail());
    }
    
    
    public static List<Location> queryLocations() {
            return db.findAll();
    }
    
*/
	@ServiceMethod
	public Location createLocation() {
		return new Location();
	}

	@ServiceMethod
	public Location readLocation(String userEmail) {
		return db.find(userEmail);
	}

	@ServiceMethod
	public Location updateLocation(Location location) {
		return db.update(location);
	}

	@ServiceMethod
	public void deleteLocation(String userEmail) {
		db.delete(userEmail);

	}

	@ServiceMethod
	public List<Location> queryLocations() {
		return db.findAll();
	}

}
