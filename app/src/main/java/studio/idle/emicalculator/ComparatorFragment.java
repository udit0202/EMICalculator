package studio.idle.emicalculator;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.NumberFormat;

import studio.idle.emicalculator.common.CommonConstants;
import studio.idle.emicalculator.common.EMIHelper;

/**
 * Created by ujain on 2/20/15.
 */
public class ComparatorFragment extends Fragment implements TextWatcher, View.OnClickListener{

    View comparatorView;
    EditText amountET1, interestET1, downPaymentET1, tenureET1;
    EditText amountET2, interestET2, downPaymentET2, tenureET2;
    TextView resultHeadingTV;
    Long principalAmount1, downPayment1, emiRounded1, totalAmountPayable1, totalInterest1;
    Long principalAmount2, downPayment2, emiRounded2, totalAmountPayable2, totalInterest2;
    Double emi1, emi2;
    float interestRate1, interestRate2;
    int tenure1, tenure2;
    boolean isTenureInMonths = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        comparatorView = inflater.inflate(R.layout.fragment_tab_two, container, false);
        Button compareButton = (Button) comparatorView.findViewById(R.id.compareButton);
        Button resetButton = (Button) comparatorView.findViewById(R.id.resetButton);

        amountET1 = (EditText) comparatorView.findViewById(R.id.amount1);
        interestET1 = (EditText) comparatorView.findViewById(R.id.interestRate1);
        downPaymentET1 = (EditText) comparatorView.findViewById(R.id.downPayment1);
        tenureET1 = (EditText) comparatorView.findViewById(R.id.tenure1);
        amountET2 = (EditText) comparatorView.findViewById(R.id.amount2);
        interestET2 = (EditText) comparatorView.findViewById(R.id.interestRate2);
        downPaymentET2 = (EditText) comparatorView.findViewById(R.id.downPayment2);
        tenureET2 = (EditText) comparatorView.findViewById(R.id.tenure2);

        Spinner spinner = (Spinner) comparatorView.findViewById(R.id.tenureSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.tenure_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        amountET1.addTextChangedListener(this);
        interestET1.addTextChangedListener(this);
        downPaymentET1.addTextChangedListener(this);
        tenureET1.addTextChangedListener(this);
        amountET2.addTextChangedListener(this);
        interestET2.addTextChangedListener(this);
        downPaymentET2.addTextChangedListener(this);
        tenureET2.addTextChangedListener(this);

        compareButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        resultHeadingTV = (TextView) comparatorView.findViewById(R.id.compareResultHeading);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getResources().getAssets(), "fonts/girls.ttf");
        resultHeadingTV.setTypeface(custom_font);

        return comparatorView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        int currentInputHashCode = editable.hashCode();

        if (currentInputHashCode == amountET1.getText().hashCode()) {
            validateAmount(amountET1.getText(), amountET1);
        } else if (currentInputHashCode == amountET2.getText().hashCode()) {
            validateAmount(amountET2.getText(), amountET2);
        }  else if (currentInputHashCode == interestET1.getText().hashCode()) {
            validateInterestRate(interestET1.getText(), interestET1);
        } else if (currentInputHashCode == interestET2.getText().hashCode()) {
            validateInterestRate(interestET2.getText(), interestET2);
        } else if (currentInputHashCode == downPaymentET1.getText().hashCode()) {
            validateDownPayment(downPaymentET1.getText(), downPaymentET1);
        } else if (currentInputHashCode == downPaymentET2.getText().hashCode()) {
            validateDownPayment(downPaymentET2.getText(), downPaymentET2);
        } else if (currentInputHashCode == tenureET1.getText().hashCode()) {
            validateTenure(tenureET1.getText(), tenureET1);
        } else if (currentInputHashCode == tenureET2.getText().hashCode()) {
            validateTenure(tenureET2.getText(), tenureET2);
        }
    }

    private void validateInterestRate(Editable text, EditText editText) {
        if (!text.toString().equals("")) {
            Float interestRate = Float.parseFloat(text.toString());
            if (interestRate == 0F) {
                editText.setError("Enter rate % > 0");
            } else if (interestRate > 100F) {
                editText.setError("Enter rate % < 100");
            } else {
                editText.setError(null);
            }
        } else {
            editText.setError(null);
        }
    }

    private void validateDownPayment(Editable text, EditText editText) {
        if (!text.toString().equals("") && text.toString().length() > 15) {
            editText.setError("Down Payment too Large");
        } else {
            editText.setError(null);
        }
    }

    private void validateTenure(Editable text, EditText editText) {
        int validPeriod = 100; //years
        if(isTenureInMonths) {
            validPeriod = validPeriod * 12;
        }

        if (!text.toString().equals("") && Integer.parseInt(text.toString()) > validPeriod) {
            editText.setError("Invalid tenure period, enter smaller period");
        } else {
            editText.setError(null);
        }
    }

    private void validateAmount(Editable text, EditText editText) {
        if (!text.toString().equals("") && text.toString().length() > 15) {
            editText.setError("Amount too Large");
        } else {
            editText.setError(null);
        }
    }

    public void resetFields() {
        amountET1.setText(null);
        amountET2.setText(null);
        interestET1.setText(null);
        interestET2.setText(null);
        downPaymentET1.setText(null);
        downPaymentET2.setText(null);
        tenureET1.setText(null);
        tenureET2.setText(null);

        amountET1.setError(null);
        amountET2.setError(null);
        interestET1.setError(null);
        interestET2.setError(null);
        downPaymentET1.setError(null);
        downPaymentET2.setError(null);
        tenureET1.setError(null);
        tenureET1.setError(null);

        EMIHelper.hideKeyboard(getActivity());
        hideResultLayout();
    }

    public void hideResultLayout() {
        comparatorView.findViewById(R.id.compareResultHeading).setVisibility(View.INVISIBLE);
        comparatorView.findViewById(R.id.resultLayout).setVisibility(View.INVISIBLE);
        comparatorView.findViewById(R.id.resultFooter).setVisibility(View.INVISIBLE);
        comparatorView.findViewById(R.id.mainLayout).setBackgroundResource(R.color.main_background_color);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resetButton:
                resetFields();
                break;
            case R.id.compareButton:
                compareEMI();
                break;
        }
    }

    private void compareEMI() {
        //hide keyboard
        EMIHelper.hideKeyboard(getActivity());

        if(validateInputs()) {
            //Loan1
            principalAmount1 = Long.parseLong(amountET1.getText().toString());
            interestRate1 = Float.parseFloat(interestET1.getText().toString());

            tenure1 = Integer.parseInt(tenureET1.getText().toString());
            if (!isTenureInMonths) {
                tenure1 *= 12;
            }

            if (!("").equals(downPaymentET1.getText().toString())) {
                downPayment1 = Long.parseLong(downPaymentET1.getText().toString());
            } else {
                downPayment1 = 0L;
            }

            emi1 = EMIHelper.calculateEMI(principalAmount1, interestRate1, downPayment1, tenure1);
            emiRounded1 = Math.round(emi1);
            totalAmountPayable1 = Math.round(emi1 * tenure1);
            totalInterest1 = totalAmountPayable1 - (principalAmount1 - downPayment1);

            TextView monthlyInstallmentTV1 = (TextView) comparatorView.findViewById(R.id.monthlyInstallment1);
            TextView totalInterestTV1 = (TextView) comparatorView.findViewById(R.id.totalInterest1);
            TextView totalAmountTV1 = (TextView) comparatorView.findViewById(R.id.totalAmount1);
            NumberFormat currencyFormatter = NumberFormat.getNumberInstance();
            monthlyInstallmentTV1.setText(currencyFormatter.format(emiRounded1));
            totalInterestTV1.setText(currencyFormatter.format(totalInterest1));
            totalAmountTV1.setText(currencyFormatter.format(totalAmountPayable1));

            //Loan2
            principalAmount2 = Long.parseLong(amountET2.getText().toString());
            interestRate2 = Float.parseFloat(interestET2.getText().toString());

            tenure2 = Integer.parseInt(tenureET2.getText().toString());
            if (!isTenureInMonths) {
                tenure2 *= 12;
            }

            if (!("").equals(downPaymentET2.getText().toString())) {
                downPayment2 = Long.parseLong(downPaymentET2.getText().toString());
            } else {
                downPayment2 = 0L;
            }

            emi2 = EMIHelper.calculateEMI(principalAmount2, interestRate2, downPayment2, tenure2);
            emiRounded2 = Math.round(emi2);
            totalAmountPayable2 = Math.round(emi2 * tenure2);
            totalInterest2 = totalAmountPayable2 - (principalAmount2 - downPayment2);

            TextView monthlyInstallmentTV2 = (TextView) comparatorView.findViewById(R.id.monthlyInstallment2);
            TextView totalInterestTV2 = (TextView) comparatorView.findViewById(R.id.totalInterest2);
            TextView totalAmountTV2 = (TextView) comparatorView.findViewById(R.id.totalAmount2);
            monthlyInstallmentTV2.setText(currencyFormatter.format(emiRounded2));
            totalInterestTV2.setText(currencyFormatter.format(totalInterest2));
            totalAmountTV2.setText(currencyFormatter.format(totalAmountPayable2));

            TextView differenceInterest = (TextView) comparatorView.findViewById(R.id.differenceInterest);
            differenceInterest.setText(currencyFormatter.format(Math.abs(totalInterest1 - totalInterest2)));
            resultHeadingTV.setVisibility(View.VISIBLE);
            comparatorView.findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);
            comparatorView.findViewById(R.id.resultFooter).setVisibility(View.VISIBLE);
            comparatorView.findViewById(R.id.mainLayout).setBackgroundResource(R.color.main_background_post_color);

        }
    }

    public boolean validateInputs() {
        boolean valid = true;
        String alertMessage = "";

        if (amountET1.getError() != null || interestET1.getError() != null || downPaymentET1.getError() != null || tenureET1.getError() != null
                || amountET2.getError() != null || interestET2.getError() != null || downPaymentET2.getError() != null || tenureET2.getError() != null ) {
            ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
            dialogFragment.setMessage("Please correct Errors");
            dialogFragment.setTitle("Invalid Inputs");
            dialogFragment.show(getFragmentManager(), "MyDialog");
            valid = false;
        } else {
            if (amountET1.getText().toString().equals("")) {
                valid = false;
                alertMessage = alertMessage.concat(CommonConstants.PRINCIPAL_AMOUNT);
            }
            if (amountET2.getText().toString().equals("")) {
                valid = false;
                alertMessage = alertMessage.concat(CommonConstants.PRINCIPAL_AMOUNT);
            }
            if (interestET1.getText().toString().equals("")) {
                if (!valid)
                    alertMessage = alertMessage.concat(", ").concat(CommonConstants.INTEREST_RATE);
                else {
                    valid = false;
                    alertMessage = CommonConstants.INTEREST_RATE;
                }
            }
            if (interestET2.getText().toString().equals("")) {
                if (!valid)
                    alertMessage = alertMessage.concat(", ").concat(CommonConstants.INTEREST_RATE);
                else {
                    valid = false;
                    alertMessage = CommonConstants.INTEREST_RATE;
                }
            }
            if (tenureET1.getText().toString().equals("")) {
                if (!valid)
                    alertMessage = alertMessage.concat(", ").concat(CommonConstants.TENURE);
                else {
                    valid = false;
                    alertMessage = CommonConstants.TENURE;
                }
            }
            if (tenureET2.getText().toString().equals("")) {
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
            } else if (!downPaymentET1.getText().toString().equals("")) {
                downPayment1 = Long.parseLong(downPaymentET1.getText().toString());
                if (downPayment1 >= Long.parseLong(amountET1.getText().toString())) {
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
}
