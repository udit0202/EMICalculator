package studio.idle.emicalculator.common;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.NumberFormat;

/**
 * Created by ujain on 2/21/15.
 */
public  class EMIHelper {

    public static Double calculateEMI(Long principalAmount, float rateOfInterest, Long downPayment, int tenureInMonths) {
        Double emi = ((principalAmount - downPayment) * (rateOfInterest/(12*100)))*((Math.pow((1+(rateOfInterest/(12*100))),tenureInMonths)))/((Math.pow((1+(rateOfInterest/(12*100))),tenureInMonths)-1));
        return emi;
    }

    public static String constructShareText(Long principalAmount, float interestRate, Long downPayment, int tenure, boolean isTenureInMonths, Long emiRounded, Long totalInterest, Long totalAmountPayable) {
        NumberFormat currencyFormatter = NumberFormat.getNumberInstance();
        String tenureType ;
        tenureType = isTenureInMonths ? "Months": "Years";
        String shareText = "";
        shareText += "Loan Amount :" + currencyFormatter.format(principalAmount - downPayment) + "\n" + "Interest Rate : " + interestRate + " %\n" + "Tenure : " + tenure + " " + tenureType + "\n\n";
        shareText += "EMI Calculation: \n\n";
        shareText += "EMI Amount: " + currencyFormatter.format(emiRounded)+ "\n" + "Total Interest Payable: " + currencyFormatter.format(totalInterest)+ "\n" + "Total Amount Payable: " + currencyFormatter.format(totalAmountPayable) + "\n\n";
        return shareText;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
