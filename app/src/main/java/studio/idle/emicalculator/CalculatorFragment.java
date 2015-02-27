package studio.idle.emicalculator;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import studio.idle.emicalculator.common.CommonConstants;
import studio.idle.emicalculator.common.EMIHelper;

/**
 * Created by ujain on 2/20/15.
 */
public class CalculatorFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, TextWatcher {

    View calculatorView;
    EditText amountET, interestET, downPaymentET, tenureET;
    Long principalAmount, downPayment, emiRounded, totalAmountPayable, totalInterest ;
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

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
        calculatorView.findViewById(R.id.resultLayout).setVisibility(View.INVISIBLE);
        calculatorView.findViewById(R.id.resultHeading).setVisibility(View.INVISIBLE);
    }

    public void shareResults() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, EMIHelper.constructShareText(principalAmount, interestRate, downPayment, tenure, isTenureInMonths, emiRounded, totalInterest, totalAmountPayable));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void calculateEMI() {
            hideKeyboard();

            if(validateInputs(amountET, interestET, downPaymentET, tenureET)) {
                principalAmount = Long.parseLong(amountET.getText().toString());
                interestRate = Float.parseFloat(interestET.getText().toString());

                if(!("").equals(downPaymentET.getText().toString())) {
                    downPayment = Long.parseLong(downPaymentET.getText().toString());
                    } else {
                        downPayment = 0L;
                }

                tenure = Integer.parseInt(tenureET.getText().toString());
                if (!isTenureInMonths) {
                    tenure *= 12;
                }

                emi = EMIHelper.calculateEMI(principalAmount, interestRate, downPayment, tenure);
                emiRounded = Math.round(emi);
                totalAmountPayable = Math.round(emi * tenure);
                totalInterest = totalAmountPayable - ( principalAmount - downPayment);

                TextView monthlyInstallmentTV = (TextView) calculatorView.findViewById(R.id.monthlyInstallment);
                TextView totalInterestTV = (TextView) calculatorView.findViewById(R.id.totalInterest);
                TextView totalAmountTV = (TextView) calculatorView.findViewById(R.id.totalAmountPayable);

                monthlyInstallmentTV.setText(emiRounded.toString());
                totalAmountTV.setText(totalAmountPayable.toString());
                totalInterestTV.setText(totalInterest.toString());

                calculatorView.findViewById(R.id.resultHeading).setVisibility(View.VISIBLE);
                calculatorView.findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);
                calculatorView.findViewById(R.id.mainLayout).setBackgroundResource(R.color.darkgrey);
        }
    }

    public boolean validateInputs(EditText amountET, EditText rateET, EditText downPayment, EditText tenure) {
        boolean valid = true;
        String alertMessage = "";
        Long zeroValue = 0L;

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
        if(tenure.getText().toString().equals("")){
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
            dialogFragment.show(getFragmentManager(), "MyDialog");
        }
        return valid;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.resetButton : resetFields();
                break;
            case R.id.calculateButton : calculateEMI();
                break;
            case R.id.shareButton : shareResults();
        }
    }

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
            if(interestRate == 0F){
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
        if (!text.toString().equals("")) {
            if (text.toString().length() > 20) {
                downPaymentET.setError("Down Payment too Large");
            } else if (Long.parseLong(text.toString()) > Long.parseLong(amountET.getText().toString())) {
                downPaymentET.setError("Cannot be more than principal");
            }
            else {
                downPaymentET.setError(null);
            }
        } else {
            downPaymentET.setError(null);
        }
    }

    private void validateTenure(Editable text) {
    }

    private void validateAmount(Editable text) {
        if (!text.toString().equals("")) {
            if (text.toString().length() > 15) {
                amountET.setError("Amount too Large");
            } else {
                amountET.setError(null);
            }
        } else {
            amountET.setError(null);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.tenure_months :
                isTenureInMonths = true;
                break;
            case R.id.tenure_years :
                isTenureInMonths = false;
        }
    }

}