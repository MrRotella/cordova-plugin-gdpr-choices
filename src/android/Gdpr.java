package it.federico.rota.cordova;

import android.content.Context;
import android.content.SharedPreferences;
// import android.preference.PreferenceManager;
import androidx.preference.PreferenceManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;

public class Gdpr extends CordovaPlugin {
  private static String TAG = "GdprChoices";
  private JSONObject response = new JSONObject();

  private boolean hasAttribute(String input, int index) {
    if (input == null) {
      LOG.d(TAG, "hasAttribute: index " + index + " input is NULL!");
      return false;
    }
    if( input.length() >= index ) {
      LOG.d(TAG, "hasAttribute: index " + index + " input " + input + " input.charAt(index-1) " + input.charAt(index-1));
    } else {
      LOG.d(TAG, "hasAttribute: index " + index + " is MISSING!");
    }
    return input.length() >= index && input.charAt(index-1) == '1';
  }

  private boolean hasConsentFor(List<Integer> indexes, String purposeConsent, boolean hasVendorConsent) {
      for (Integer p: indexes) {
          if (!hasAttribute(purposeConsent, p)) {
              LOG.d(TAG, "hasConsentFor: denied for purpose #" + p );
              return false;
          }
      }
      return hasVendorConsent;
  }

  private boolean hasConsentOrLegitimateInterestFor(List<Integer> indexes, String purposeConsent, String purposeLI, boolean hasVendorConsent, boolean hasVendorLI){
      for (Integer p: indexes) {
          boolean purposeAndVendorLI = hasAttribute(purposeLI, p) && hasVendorLI;
          boolean purposeConsentAndVendorConsent = hasAttribute(purposeConsent, p) && hasVendorConsent;
          boolean isOk = purposeAndVendorLI || purposeConsentAndVendorConsent;
          if (!isOk){
              LOG.d(TAG, "hasConsentOrLegitimateInterestFor: denied for #" + p);
              return false;
          }
      }
      return true;
  }
  
  private boolean canShowAds(Context context){
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    String purposeConsent = prefs.getString("IABTCF_PurposeConsents", "");
    LOG.d(TAG, "purposeConsent " + purposeConsent);
    String vendorConsent = prefs.getString("IABTCF_VendorConsents","");
    LOG.d(TAG, "vendorConsent " + vendorConsent);
    String vendorLI = prefs.getString("IABTCF_VendorLegitimateInterests","");
    LOG.d(TAG, "vendorLI " + vendorLI);
    String purposeLI = prefs.getString("IABTCF_PurposeLegitimateInterests","");
    LOG.d(TAG, "purposeLI " + purposeLI);

    int googleId = 755;
    boolean hasGoogleVendorConsent = hasAttribute(vendorConsent, googleId);
    LOG.d(TAG, "hasGoogleVendorConsent " + hasGoogleVendorConsent);
    boolean hasGoogleVendorLI = hasAttribute(vendorLI, googleId);
    LOG.d(TAG, "hasGoogleVendorLI " + hasGoogleVendorLI);

    List<Integer> indexes = new ArrayList<>();
    indexes.add(1);

    List<Integer> indexesLI = new ArrayList<>();
    indexesLI.add(2);
    indexesLI.add(7);
    indexesLI.add(9);
    indexesLI.add(10);

    return hasConsentFor(indexes, purposeConsent, hasGoogleVendorConsent)
            && hasConsentOrLegitimateInterestFor(indexesLI, purposeConsent, purposeLI, hasGoogleVendorConsent, hasGoogleVendorLI);

  }
  
  private boolean canShowPersonalizedAds(Context context){
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    String purposeConsent = prefs.getString("IABTCF_PurposeConsents", "");
    String vendorConsent = prefs.getString("IABTCF_VendorConsents","");
    String vendorLI = prefs.getString("IABTCF_VendorLegitimateInterests","");
    String purposeLI = prefs.getString("IABTCF_PurposeLegitimateInterests","");

    int googleId = 755;
    boolean hasGoogleVendorConsent = hasAttribute(vendorConsent, googleId);
    boolean hasGoogleVendorLI = hasAttribute(vendorLI, googleId);

    List<Integer> indexes = new ArrayList<>();
    indexes.add(1);
    indexes.add(3);
    indexes.add(4);

    List<Integer> indexesLI = new ArrayList<>();
    indexesLI.add(2);
    indexesLI.add(7);
    indexesLI.add(9);
    indexesLI.add(10);

    return hasConsentFor(indexes, purposeConsent, hasGoogleVendorConsent)
            && hasConsentOrLegitimateInterestFor(indexesLI, purposeConsent, purposeLI, hasGoogleVendorConsent, hasGoogleVendorLI);

  }

  private int getGdprApplies(Context context){
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    int gdprApplies = prefs.getInt("IABTCF_gdprApplies", -1);
    return gdprApplies;
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    Context context = this.cordova.getActivity();
    LOG.d(TAG,"execute");
    response.put("success",false);
    response.put("value",null);
    response.put("message","NO action recognized");

    
    if( action.equals("checkPlugin") ) {
      LOG.d(TAG,"action checkPlugin");
      response.put("success",true);
      response.put("message","plugin OK");
      callbackContext.success(response);
      return true;
    }
    if( action.equals("isGdpr") ) {
      LOG.d(TAG,"action isGdpr");
      int gdprApplies = this.getGdprApplies(context);
      LOG.d(TAG,"gdprApplies response"+gdprApplies);
      if(gdprApplies<0){
        response.put("success",false);
        response.put("message","unable to determine if can apply GDPR");
      } else {
        response.put("success",true);
        response.put("value",gdprApplies);
        response.put("message","response is ("+gdprApplies+")");
      }
      callbackContext.success(response);
      return true;
    }
    if( action.equals("canShowAds") ) {
      LOG.d(TAG,"action canShowAds");
      boolean canShowAds = this.canShowAds(context);
      LOG.d(TAG,"canShowAds response"+canShowAds);
      /* if(gdprApplies<0){
        response.put("success",false);
        response.put("message","unable to determine if can apply GDPR");
      } else { */
        response.put("success",true);
        response.put("value",canShowAds);
        response.put("message","response is ("+canShowAds+")");
      /* } */
      callbackContext.success(response);
      return true;
    }
    if( action.equals("canShowPersonalizedAds") ) {
      LOG.d(TAG,"action canShowPersonalizedAds");
      boolean canShowPersonalizedAds = this.canShowPersonalizedAds(context);
      LOG.d(TAG,"canShowPersonalizedAds response"+canShowPersonalizedAds);
      /* if(gdprApplies<0){
        response.put("success",false);
        response.put("message","unable to determine if can apply GDPR");
      } else { */
        response.put("success",true);
        response.put("value",canShowPersonalizedAds);
        response.put("message","response is ("+canShowPersonalizedAds+")");
      /* } */
      callbackContext.success(response);
      return true;
    }
    LOG.d(TAG,"NO action recognized");
    callbackContext.success(response);
    return true;
  }
  
}

