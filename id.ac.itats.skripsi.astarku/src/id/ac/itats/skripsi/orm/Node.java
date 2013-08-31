package id.ac.itats.skripsi.orm;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import id.ac.itats.skripsi.orm.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table NODE.
 */
public class Node implements Comparable<Node>{

    private Long nodeID;
    private String latitude;
    private String longitude;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient NodeDao myDao;

    private List<Way> sourceAdjacencies;
    private List<Way> targetAdjacencies;
    
	public double minDistance = Double.POSITIVE_INFINITY;
	public Node previous;

    public Node() {
    }

    public Node(Long nodeID) {
        this.nodeID = nodeID;
    }

    public Node(Long nodeID, String latitude, String longitude) {
        this.nodeID = nodeID;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNodeDao() : null;
    }

    public Long getNodeID() {
        return nodeID;
    }

    public void setNodeID(Long nodeID) {
        this.nodeID = nodeID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Way> getSourceAdjacencies() {
        if (sourceAdjacencies == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            WayDao targetDao = daoSession.getWayDao();
            List<Way> sourceAdjacenciesNew = targetDao._queryNode_SourceAdjacencies(nodeID);
            synchronized (this) {
                if(sourceAdjacencies == null) {
                    sourceAdjacencies = sourceAdjacenciesNew;
                }
            }
        }
        return sourceAdjacencies;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetSourceAdjacencies() {
        sourceAdjacencies = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Way> getTargetAdjacencies() {
        if (targetAdjacencies == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            WayDao targetDao = daoSession.getWayDao();
            List<Way> targetAdjacenciesNew = targetDao._queryNode_TargetAdjacencies(nodeID);
            synchronized (this) {
                if(targetAdjacencies == null) {
                    targetAdjacencies = targetAdjacenciesNew;
                }
            }
        }
        return targetAdjacencies;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetTargetAdjacencies() {
        targetAdjacencies = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

	@Override
	public int compareTo(Node other) {
		return Double.compare(minDistance, other.minDistance);
	}

	 public Point getPoint() {
        Point p = new GeometryFactory().createPoint(
                new Coordinate(Double.valueOf(longitude), Double.valueOf(latitude)));

        return p;
    }
	
	

}
