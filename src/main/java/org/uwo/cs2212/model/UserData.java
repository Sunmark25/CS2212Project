package org.uwo.cs2212.model;

import org.uwo.cs2212.CurrentUser;
import org.uwo.cs2212.SearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents user-specific data, such as user-created layers and favorite Points of Interest (POIs).
 *
 * @author Yaopeng Xie
 * @author Jarrett Boersen
 * @author Tingrui Zhang
 */
public class UserData {
    private List<UserLayer> userLayers;
    private List<FavoritePoi> favoritePois;

    public UserData() {
        this.userLayers = new ArrayList<>();
        this.favoritePois = new ArrayList<>();
    }
    public List<UserLayer> getUserLayers() {
        return userLayers;
    }

    public void setUserLayers(List<UserLayer> userLayers) {
        this.userLayers = userLayers;
    }


    /**
     * This method is used to add a Point of Interest (POI) to the corresponding
     * UserLayer for the given BaseMap and FloorMap.
     *
     * @param baseMap the BaseMap containing the FloorMap
     * @param floorMap the FloorMap where the POI is located
     * @param poi the Point of Interest to be added
     */
    public void addPoi(BaseMap baseMap, FloorMap floorMap, PointOfInterest poi){
        // Used to store if a new userLayer had to be created
        boolean newUserLayerCreated = false;

        UserLayer userLayer = findUserLayer(baseMap, floorMap, this);
        if (userLayer == null){
            newUserLayerCreated = true;
            userLayer = new UserLayer();
            userLayer.setBaseName(baseMap.getName());
            userLayer.setFloorName(floorMap.getName());
            userLayer.setName("User layer");
            userLayer.setHideLayer(false);
            userLayer.setColor("BLACK");
            userLayer.setFont("Arial");
            userLayer.setLayerType("User layer"); // TODO: Correct?
            userLayer.setSize(16);
            userLayer.setPoints(new ArrayList<>());
        }

        // Add the poi to the userLayer
        userLayer.getPoints().add(poi);

        // If userLayers is empty create a new list
        if(userLayers == null){
            userLayers = new ArrayList<>();
        }

        // If a new user layer was created then add it to the userLayers
        if (newUserLayerCreated) {
            userLayers.add(userLayer);
        } else { // Otherwise, update the existing layer
            updateUserLayers(userLayer);
        }
    }

    /**
     *  This method is used to remove a Point of Interest (POI) from the corresponding
     *  UserLayer for the given BaseMap and FloorMap and POI.
     *
     * @param baseMap The BaseMap the POI is on
     * @param floorMap The FloorMap the POI is on
     * @param poiToRemove The POI to be removed
     */
    public void removePOI(BaseMap baseMap, FloorMap floorMap, PointOfInterest poiToRemove) {
        // Find the userLayer the POI in
        UserLayer userLayer = findUserLayer(baseMap, floorMap, this);

        // Loop through all the points in the userLayer
        for (PointOfInterest point : userLayer.getPoints()) {
            // If the point is equal to the given poiToRemove then remove the point from the userLayer
            if (point.equals(poiToRemove)) {
                userLayer.getPoints().remove(point);
                break; // Leave loop early since point was removed
            }
        }
    }

    /**
     * This method is used to edit a given POI in the corresponding UserLayer for the given BaseMap and FloorMap
     *
     * @param baseMap The BaseMap the POI is on
     * @param floorMap The FloorMap the POI is on
     * @param oldPOI The POI that is being updated
     * @param updatedPOI The updated POI (this is a new POI instead of editing the old one to allow searching for the old POI)
     */
    public void editPOI(BaseMap baseMap, FloorMap floorMap, PointOfInterest oldPOI, PointOfInterest updatedPOI) {
        // Find the userLayer the POI in
        UserLayer userLayer = findUserLayer(baseMap, floorMap, this);

        // Loop through all the points in the userLayer
        for (PointOfInterest point : userLayer.getPoints()) {
            // If the point is equal to the given oldPOI then set the point to updatedPOI
            if (point.equals(oldPOI)) {
                // Get the index of the matching point
                int index = userLayer.getPoints().indexOf(point);
                // Set the old point (selected poi) to the updatedPOI
                userLayer.getPoints().set(index, updatedPOI);
                break; // Leave loop early since point was edited
            }
        }
    }

    /**
     * This method is to update a UserLayer in the list with a new version.
     *
     * @param updatedUserLayer the updated UserLayer to replace the old one in the list
     */
    public void updateUserLayers(UserLayer updatedUserLayer) {
        // Loop through all UserLayers in the list
        for (UserLayer userLayer : userLayers) {
            // If the userLayer equals the updated one then get the index of current userLayer in the list,
            // and call the set method to replace the old userLayer with the updated one
            if (userLayer.equals(updatedUserLayer)) {
                int index = userLayers.indexOf(userLayer);
                userLayers.set(index, updatedUserLayer);
                break; // Break early since the layer was found
            }
        }
    }

    /**
     * Removes a favorite POI from the favoritePois list.
     *
     * @param poi The POI to remove from the favoritePois list
     * @param baseMap The BaseMap for the POI to be removed
     * @param floorMap The FloorMap for the POI to be removed
     */
    public void removeFavourite(PointOfInterest poi, BaseMap baseMap, FloorMap floorMap) {
        // Loop through the list of the favouritePois
        for (FavoritePoi favPoi : favoritePois) {
            // If favPoi matches the given names then remove the favourite at the index
            if (favPoi.getPoiName().equals(poi.getName()) && favPoi.getBaseMapName().equals(baseMap.getName()) &&
                    favPoi.getFloorMapName().equals(floorMap.getName())) {
                favoritePois.remove(favPoi);
                break; // Break early since the layer was found
            }
        }

        // Save the updated object to the json file
        CurrentUser.saveUserData();
    }

    /**
     * Adds a favorite POI to the favoritePois list.
     *
     * @param poi The POI to add to the favoritePois list
     * @param baseMap The BaseMap for the POI to be added
     * @param floorMap The FloorMap for the POI to be added
     */
    public void addFavourite(PointOfInterest poi, BaseMap baseMap, Layer layer, FloorMap floorMap) {
        // Create a new favourite
        FavoritePoi favoritePoi = new FavoritePoi(baseMap.getName(), floorMap.getName(), layer.getName(), poi.getName());
        // Add it to the user's list
        favoritePois.add(favoritePoi);

        // Save the updated object to the json file
        CurrentUser.saveUserData();
    }

    public static UserLayer findUserLayer(BaseMap baseMap, FloorMap floorMap, UserData userLayerList){
        if (userLayerList != null && userLayerList.getUserLayers() != null){
            for(UserLayer userLayer : userLayerList.getUserLayers()){
                if(userLayer.getFloorName().equals(floorMap.getName()) && userLayer.getBaseName().equals(baseMap.getName())){
                    return userLayer;
                }
            }
        }
        return null;
    }

    public List<FavoritePoi> getFavoritePois() {
        return favoritePois;
    }

    public void setFavoritePois(List<FavoritePoi> favoritePois) {
        this.favoritePois = favoritePois;
    }
}
