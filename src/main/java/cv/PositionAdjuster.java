package cv;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;

public class PositionAdjuster {
    public PositionAdjuster() {
    }

    private boolean isValid(IPresentation presentation) {
        return true;
    }

    public void adjust(List<ILinkPresentation> targets) {
        Collections.sort(targets, new LinkComparator());
        for (ILinkPresentation link: targets ) {
            adjust(link);
        }
    }
    
    private void adjust(ILinkPresentation link) {
        if(!isValid(link)) {
            return;
        }
        try {
            if (getAspectRatio(link) < 1.0) {
                adjustVertical(link);
            } else {
                adjustHorizontal(link);
            }
        } catch (InvalidEditingException e) {
            e.printStackTrace();
        }
    }
    
    private void adjustVertical(ILinkPresentation link) throws InvalidEditingException {
        INodePresentation targetEnd = (INodePresentation) link.getTargetEnd();
        INodePresentation sourceEnd = (INodePresentation) link.getSourceEnd();
        Point2D targetLocation = targetEnd.getLocation();
        Point2D sourceLocation = sourceEnd.getLocation();
        Point2D targetPoint = getCenterPoint(targetEnd);
        Point2D sourcePoint = getCenterPoint(sourceEnd);
        if(targetEnd == null || sourceEnd == null) {
            return;
        }

        double diff = targetPoint.getX() - sourcePoint.getX();
        if (sourcePoint.getY() < targetPoint.getY()) {
            System.out.println("adjustVertical::sourceY < targetY");
            targetEnd.setLocation(new Point2D.Double(targetLocation.getX() - diff, targetLocation.getY()));
        } else {
            System.out.println("adjustVertical:: ! sourceY < targetY");
            sourceEnd.setLocation(new Point2D.Double(sourceLocation.getX() - diff, sourceLocation.getY()));
        }
    }

    private void adjustHorizontal(ILinkPresentation link) throws InvalidEditingException {
        INodePresentation targetEnd = (INodePresentation) link.getTargetEnd();
        INodePresentation sourceEnd = (INodePresentation) link.getSourceEnd();
        Point2D targetLocation = targetEnd.getLocation();
        Point2D sourceLocation = sourceEnd.getLocation();
        Point2D targetPoint = getCenterPoint(targetEnd);
        Point2D sourcePoint = getCenterPoint(sourceEnd);
        if(targetEnd == null || sourceEnd == null) {
            return;
        }

        double diff = targetPoint.getY() - sourcePoint.getY();
        if (sourcePoint.getX() < targetPoint.getX()) {
            System.out.println("adjustHorizontal::sourceX < targetX");
            targetEnd.setLocation(new Point2D.Double(targetLocation.getX(), targetLocation.getY() - diff));
        } else {
            System.out.println("adjustHorizontal:: ! sourceX < targetX");
            sourceEnd.setLocation(new Point2D.Double(sourceLocation.getX(), sourceLocation.getY() - diff));
        }
    }
    
    private Point2D getCenterPoint(IPresentation presentation) {
        if(!(presentation instanceof INodePresentation)) {
            return null;
        }
        INodePresentation node = (INodePresentation) presentation;
        Point2D coord = node.getLocation();
        return new Point2D.Double(coord.getX() + node.getWidth()/2 , coord.getY() + node.getHeight()/2);
    }

    private double getAspectRatio(ILinkPresentation link) {
        Point2D[] points = link.getPoints();
        Point2D sourcePoint = points[0];
        Point2D targetPoint = points[points.length -1 ];
        double w = Math.abs(sourcePoint.getX() - targetPoint.getX());
        double h = Math.abs(sourcePoint.getY() - targetPoint.getY());
        return w/h;
    }

    class LinkComparator implements Comparator<ILinkPresentation> {

        @Override
        public int compare(ILinkPresentation a, ILinkPresentation b) {
            boolean aIsVertical = getAspectRatio(a) < 1.0;
            boolean bIsVertical = getAspectRatio(b) < 1.0;
            if (aIsVertical && !bIsVertical) {
                return -1;
            }
            if (!aIsVertical && bIsVertical) {
                return 1;
            }
            
            if (aIsVertical) {
                return (int) (getMinY(a) - getMinY(b));
            } else {
                return (int) (getMinX(a) - getMinX(b));
            }
        }
        
        public double getMinY(ILinkPresentation link) {
            Point2D[] points = link.getPoints();
            return Math.min(points[0].getY(), points[points.length -1].getY());
        }
        
        public double getMinX(ILinkPresentation link) {
            Point2D[] points = link.getPoints();
            return Math.min(points[0].getX(), points[points.length -1].getX());
        }
    }
}
