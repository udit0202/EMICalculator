package studio.idle.emicalculator.common;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;

/**
 * Created by ujain on 2/21/15.
 */
public  class EMIHelper {

    public static Double calculateEMI(Long principalAmount, double rateOfInterest, Long downPayment, double tenureInMonths) {
        Double emi = (( (principalAmount - downPayment) * ( rateOfInterest / (12 * 100))) * ( (Math.pow((1 + (rateOfInterest / (12 * 100))), tenureInMonths))) ) /
                ( ( Math.pow((1 + (rateOfInterest / (12 * 100))), tenureInMonths)) - 1);
        return emi;
    }

    public static String constructShareText(Long principalAmount, float interestRate, Long downPayment, int tenure, boolean isTenureInMonths, Long emiRounded, Long totalInterest, Long totalAmountPayable) {
        Format indianCurrencyFormatter = new DecimalFormat("##,##,###");
        String tenureType ;
        tenureType = isTenureInMonths ? "Months": "Years";
        tenure = isTenureInMonths ? tenure: tenure/12;
        String shareText = "";
        shareText += "Loan Amount :" + indianCurrencyFormatter.format(principalAmount - downPayment) + "\n" + "Interest Rate : " + interestRate + " %\n" + "Tenure : " + tenure + " " + tenureType + "\n\n";
        shareText += "EMI Calculation: \n\n";
        shareText += "EMI Amount: " + indianCurrencyFormatter.format(emiRounded)+ "\n" + "Total Interest Payable: " + indianCurrencyFormatter.format(totalInterest)+ "\n" + "Total Amount Payable: " + indianCurrencyFormatter.format(totalAmountPayable) + "\n\n";
        return shareText;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void resetFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setText(null);
            editText.setError(null);
        }
    }

    public static void validateAmount(EditText editText) {
        if (!editText.getText().toString().equals("") && Long.parseLong(editText.getText().toString()) > CommonConstants.MAX_AMOUNT_VALUE) {
            editText.setError("Principal Amount too Large");
        } else if (!editText.getText().toString().equals("") && Long.parseLong(editText.getText().toString()) == 0) {
            editText.setError("Enter amount greater than zero");
        } else {
            editText.setError(null);
        }
    }

    public static void validateInterestRate(EditText editText) {
        if (!editText.getText().toString().equals("")) {
            Float interestRate = Float.parseFloat(editText.getText().toString());
            if (interestRate == 0F) {
                editText.setError("Enter rate % greater than 0");
            } else if (interestRate > 100F) {
                editText.setError("rate % can't be more than 100%");
            } else {
                editText.setError(null);
            }
        } else {
            editText.setError(null);
        }
    }

    public static void validateDownPayment(EditText editText) {
        if (!editText.getText().toString().equals("") && Long.parseLong(editText.getText().toString()) > CommonConstants.MAX_AMOUNT_VALUE ) {
            editText.setError("Down Payment too Large");
        } else {
            editText.setError(null);
        }
    }

    public static void validateTenure(EditText editText, boolean isTenureInMonths) {
        int validPeriod = 100; //years
        if(isTenureInMonths) {
            validPeriod = validPeriod * 12;
        }
        if ((!editText.getText().toString().equals("") && Integer.parseInt(editText.getText().toString()) == 0 )) {
            editText.setError("Invalid tenure period, cannot be zero");
        } else if (!editText.getText().toString().equals("") && Integer.parseInt(editText.getText().toString()) > validPeriod) {
            editText.setError("Invalid tenure period, enter smaller period");
        } else {
            editText.setError(null);
        }
    }

    public static String prettifyMessage(String alertMessage) {
        if(alertMessage.charAt(0) == ',') {
            alertMessage = alertMessage.substring(2);
        }
        return alertMessage;
    }
}
