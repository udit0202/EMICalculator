package studio.idle.emicalculator;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
    RadioGroup tenureRadio;
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
        tenureRadio = (RadioGroup) calculatorView.findViewById(R.id.tenureRadioGroup);

        amountET = (EditText) calculatorView.findViewById(R.id.amount);
        interestET = (EditText) calculatorView.findViewById(R.id.rateOfInterest);
        downPaymentET = (EditText) calculatorView.findViewById(R.id.downPayment);
        tenureET = (EditText) calculatorView.findViewById(R.id.tenure);

        resultHeadingTV = (TextView) calculatorView.findViewById(R.id.resultHeading);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getResources().getAssets(), "fonts/heading_font.ttf");
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
        EMIHelper.resetFields(amountET, downPaymentET, tenureET, interestET);
        RadioButton b = (RadioButton) calculatorView.findViewById(R.id.tenure_months);
        b.setChecked(true);
        EMIHelper.hideKeyboard(getActivity());
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
            tenure = isTenureInMonths ? tenure : tenure * 12;

            downPayment = ("").equals(downPaymentET.getText().toString()) ? 0L :
                    Long.parseLong(downPaymentET.getText().toString());

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
            dialogFragment.setTitle(CommonConstants.ERROR_TITLE_INVALID);
            dialogFragment.setMessage(CommonConstants.ERROR_MESSAGE_ERRORS_PRESENT);
            dialogFragment.show(getFragmentManager(), "MyDialog");
            valid = false;
        } else {
            if (amountET.getText().toString().equals("")) {
                valid = false;
                alertMessage = alertMessage.concat(", ").concat(CommonConstants.PRINCIPAL_AMOUNT);
            }
            if (rateET.getText().toString().equals("")) {
                alertMessage = alertMessage.concat(", ").concat(CommonConstants.INTEREST_RATE);
                valid = false;
            }
            if (tenure.getText().toString().equals("")) {
                alertMessage = alertMessage.concat(", ").concat(CommonConstants.TENURE);
                valid = false;
            }

            if (!valid) {
                alertMessage = EMIHelper.prettifyMessage(alertMessage);
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                dialogFragment.setMessage(alertMessage);
                dialogFragment.setTitle(CommonConstants.ERROR_TITLE_MISSING_INPUT);
                dialogFragment.show(getFragmentManager(), "MyDialog");
            } else if (!downPaymentET.getText().toString().equals("")) {
                downPayment = Long.parseLong(downPaymentET.getText().toString());
                if (downPayment >= Long.parseLong(amountET.getText().toString())) {
                    ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                    dialogFragment.setMessage(CommonConstants.ERROR_MESSAGE_DOWNPAYMENT);
                    dialogFragment.setTitle(CommonConstants.ERROR_TITLE_INVALID);
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
            EMIHelper.validateAmount(amountET);
        } else if (currentInputHashCode == interestET.getText().hashCode()) {
            EMIHelper.validateInterestRate(interestET);
        } else if (currentInputHashCode == downPaymentET.getText().hashCode()) {
            EMIHelper.validateDownPayment(downPaymentET);
        } else if (currentInputHashCode == tenureET.getText().hashCode()) {
            EMIHelper.validateTenure(tenureET, isTenureInMonths);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.tenure_months :
                isTenureInMonths = true;
                EMIHelper.validateTenure(tenureET, isTenureInMonths);
                break;
            case R.id.tenure_years :
                isTenureInMonths = false;
                EMIHelper.validateTenure(tenureET,isTenureInMonths);

        }
    }
}