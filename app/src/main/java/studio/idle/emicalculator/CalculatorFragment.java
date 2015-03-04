package studio.idle.emicalculator;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.NumberFormat;

import studio.idle.emicalculator.common.CommonConstants;
import studio.idle.emicalculator.common.EMIHelper;

/**
 * Created by ujain on 2/20/15.
 */
public class CalculatorFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, TextWatcher {

    View calculatorView;
    EditText amountET, interestET, downPaymentET, tenureET;
    TextView resultHeadingTV;

    Long principalAmount, downPayment, emiRounded, totalAmountPayable, totalInterest;
    Double emi;
    float interestRate;
    int tenure;
    boolean isTenureInMonths = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        calculatorView = inflater.inflate(R.layout.fragment_tab_one, container, false);
        Button resetButton = (Button) calculatorView.findViewById(R.id.resetButton);
        Button calculateButton = (Button) calculatorView.findViewById(R.id.calculateButton);
        Button shareButton = (Button) calculatorView.findViewById(R.id.shareButton);
        RadioGroup tenureRadio = (RadioGroup) calculatorView.findViewById(R.id.tenureRadioGroup);

        amountET = (EditText) calculatorView.findViewById(R.id.amount);
        interestET = (EditText) calculatorView.findViewById(R.id.rateOfInterest);
        downPaymentET = (EditText) calculatorView.findViewById(R.id.downPayment);
        tenureET = (EditText) calculatorView.findViewById(R.id.tenure);

        resultHeadingTV = (TextView) calculatorView.findViewById(R.id.resultHeading);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getResources().getAssets(), "fonts/girls.ttf");
        resultHeadingTV.setTypeface(custom_font);

        resetButton.setOnClickListener(this);
        calculateButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        tenureRadio.setOnCheckedChangeListener(this);

        amountET.addTextChangedListener(this);
        interestET.addTextChangedListener(this);
        downPaymentET.addTextChangedListener(this);
        tenureET.addTextChangedListener(this);

        return calculatorView;
    }

    public void resetFields() {
        amountET.setText(null);
        interestET.setText(null);
        downPaymentET.setText(null);
        tenureET.setText(null);

        amountET.setError(null);
        interestET.setError(null);
        downPaymentET.setError(null);
        tenureET.setError(null);

        hideResultLayout();
    }

    public void shareResults() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, EMIHelper.constructShareText(principalAmount, interestRate, downPayment, tenure, isTenureInMonths, emiRounded, totalInterest, totalAmountPayable));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void hideResultLayout() {
        calculatorView.findViewById(R.id.resultLayout).setVisibility(View.INVISIBLE);
        calculatorView.findViewById(R.id.resultHeading).setVisibility(View.INVISIBLE);
        calculatorView.findViewById(R.id.mainLayout).setBackgroundResource(R.color.main_background_color);
    }

    public void showResultLayout() {
        calculatorView.findViewById(R.id.resultHeading).setVisibility(View.VISIBLE);
        calculatorView.findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);
        calculatorView.findViewById(R.id.mainLayout).setBackgroundResource(R.color.main_background_post_color);
    }

    public void calculateEMI() {

        EMIHelper.hideKeyboard(getActivity());

        if (validateInputs(amountET, interestET, downPaymentET, tenureET)) {
            principalAmount = Long.parseLong(amountET.getText().toString());
            interestRate = Float.parseFloat(interestET.getText().toString());

            tenure = Integer.parseInt(tenureET.getText().toString());
            if (!isTenureInMonths) {
                tenure *= 12;
            }

            if (!("").equals(downPaymentET.getText().toString())) {
                downPayment = Long.parseLong(downPaymentET.getText().toString());
            } else {
                downPayment = 0L;
            }

            emi = EMIHelper.calculateEMI(principalAmount, interestRate, downPayment, tenure);
            emiRounded = Math.round(emi);
            totalAmountPayable = Math.round(emi * tenure);
            totalInterest = totalAmountPayable - (principalAmount - downPayment);

            TextView monthlyInstallmentTV = (TextView) calculatorView.findViewById(R.id.monthlyInstallment);
            TextView totalInterestTV = (TextView) calculatorView.findViewById(R.id.totalInterest);
            TextView totalAmountTV = (TextView) calculatorView.findViewById(R.id.totalAmountPayable);

            NumberFormat currencyFormatter = NumberFormat.getNumberInstance();
            monthlyInstallmentTV.setText(currencyFormatter.format(emiRounded));
            totalAmountTV.setText(currencyFormatter.format(totalAmountPayable));
            totalInterestTV.setText(currencyFormatter.format(totalInterest));

            showResultLayout();
        } else {
            hideResultLayout();
        }
    }

    public boolean validateInputs(EditText amountET, EditText rateET, EditText downPaymentET, EditText tenure) {
        boolean valid = true;
        String alertMessage = "";

        if (amountET.getError() != null || rateET.getError() != null || downPaymentET.getError() != null || tenure.getError() != null) {
            ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
            dialogFragment.setMessage("Please correct Errors");
            dialogFragment.setTitle("Invalid Inputs");
            dialogFragment.show(getFragmentManager(), "MyDialog");
            valid = false;
        }
        else {
            if (amountET.getText().toString().equals("")) {
                valid = false;
                alertMessage = alertMessage.concat(CommonConstants.PRINCIPAL_AMOUNT);
            }
            if (rateET.getText().toString().equals("")) {
                if (!valid)
                    alertMessage = alertMessage.concat(", ").concat(CommonConstants.INTEREST_RATE);
                else {
                    valid = false;
                    alertMessage = CommonConstants.INTEREST_RATE;
                }
            }
            if (tenure.getText().toString().equals("")) {
                if (!valid)
                    alertMessage = alertMessage.concat(", ").concat(CommonConstants.TENURE);
                else {
                    valid = false;
                    alertMessage = CommonConstants.TENURE;
                }
            }
            if (!valid) {
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                dialogFragment.setMessage(alertMessage);
                dialogFragment.setTitle("Please provide following inputs");
                dialogFragment.show(getFragmentManager(), "MyDialog");
            } else if (!downPaymentET.getText().toString().equals("")) {
                downPayment = Long.parseLong(downPaymentET.getText().toString());
                if (downPayment >= Long.parseLong(amountET.getText().toString())) {
                    ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                    dialogFragment.setMessage("Down payment amount cannot be more than or equal to loan amount");
                    dialogFragment.setTitle("Invalid Input");
                    dialogFragment.show(getFragmentManager(), "MyDialog");
                    valid = false;
                }
            }
        }
        return valid;
    }

    //OnClickListener
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.resetButton:
                resetFields();
                break;
            case R.id.calculateButton:
                calculateEMI();
                break;
            case R.id.shareButton:
                shareResults();
        }
    }

    //TextWatcher
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        int currentInputHashCode = editable.hashCode();

        if (currentInputHashCode == amountET.getText().hashCode()) {
            validateAmount(amountET.getText());
        } else if (currentInputHashCode == interestET.getText().hashCode()) {
            validateInterestRate(interestET.getText());
        } else if (currentInputHashCode == downPaymentET.getText().hashCode()) {
            validateDownPayment(downPaymentET.getText());
        } else if (currentInputHashCode == tenureET.getText().hashCode()) {
            validateTenure(tenureET.getText());
        }
    }

    private void validateInterestRate(Editable text) {
        if (!text.toString().equals("")) {
            Float interestRate = Float.parseFloat(text.toString());
            if (interestRate == 0F) {
                interestET.setError("Enter rate % > 0");
            } else if (interestRate > 100F) {
                interestET.setError("Enter rate % < 100");
            } else {
                interestET.setError(null);
            }
        } else {
            interestET.setError(null);
        }
    }

    private void validateDownPayment(Editable text) {
        if (!text.toString().equals("") && text.toString().length() > 15) {
                downPaymentET.setError("Down Payment too Large");
        } else {
            downPaymentET.setError(null);
        }
    }

    private void validateTenure(Editable text) {
        int validPeriod = 100; //years
        if(isTenureInMonths) {
            validPeriod = validPeriod * 12;
        }

        if (!text.toString().equals("") && Integer.parseInt(text.toString()) > validPeriod) {
            tenureET.setError("Invalid tenure period, enter smaller period");
        } else {
            tenureET.setError(null);
        }
    }

    private void validateAmount(Editable text) {
        if (!text.toString().equals("") && text.toString().length() > 15) {
            amountET.setError("Amount too Large");
        } else {
            amountET.setError(null);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.tenure_months:
                isTenureInMonths = true;
                break;
            case R.id.tenure_years:
                isTenureInMonths = false;
        }
    }

}