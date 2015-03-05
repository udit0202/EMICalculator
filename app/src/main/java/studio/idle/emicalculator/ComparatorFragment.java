package studio.idle.emicalculator;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class ComparatorFragment extends Fragment implements TextWatcher, View.OnClickListener {

    View comparatorView;
    TextView resultHeadingTV;
    EditText amountET1, interestET1, downPaymentET1, tenureET1, amountET2, interestET2, downPaymentET2, tenureET2;
    Long principalAmount1, downPayment1, emiRounded1, totalAmountPayable1, totalInterest1,
            principalAmount2, downPayment2, emiRounded2, totalAmountPayable2, totalInterest2;
    Double emi1, emi2;
    float interestRate1, interestRate2;
    int tenure1, tenure2;
    boolean isTenureInMonths = true;
    Spinner tenureSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        comparatorView = inflater.inflate(R.layout.fragment_tab_two, container, false);
        Button compareButton = (Button) comparatorView.findViewById(R.id.compareButton);
        Button resetButton = (Button) comparatorView.findViewById(R.id.resetButton);
        compareButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        //Loan1
        amountET1 = (EditText) comparatorView.findViewById(R.id.amount1);
        interestET1 = (EditText) comparatorView.findViewById(R.id.interestRate1);
        downPaymentET1 = (EditText) comparatorView.findViewById(R.id.downPayment1);
        tenureET1 = (EditText) comparatorView.findViewById(R.id.tenure1);
        amountET1.addTextChangedListener(this);
        interestET1.addTextChangedListener(this);
        downPaymentET1.addTextChangedListener(this);
        tenureET1.addTextChangedListener(this);

        //Loan2
        amountET2 = (EditText) comparatorView.findViewById(R.id.amount2);
        interestET2 = (EditText) comparatorView.findViewById(R.id.interestRate2);
        downPaymentET2 = (EditText) comparatorView.findViewById(R.id.downPayment2);
        tenureET2 = (EditText) comparatorView.findViewById(R.id.tenure2);
        amountET2.addTextChangedListener(this);
        interestET2.addTextChangedListener(this);
        downPaymentET2.addTextChangedListener(this);
        tenureET2.addTextChangedListener(this);

        //tenure spinner
        tenureSpinner = (Spinner) comparatorView.findViewById(R.id.tenureSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.tenure_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tenureSpinner.setAdapter(adapter);

        tenureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                isTenureInMonths = position == 0 ? true : false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        resultHeadingTV = (TextView) comparatorView.findViewById(R.id.compareResultHeading);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getResources().getAssets(), "fonts/heading_font.ttf");
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
            EMIHelper.validateAmount(amountET1);
        } else if (currentInputHashCode == amountET2.getText().hashCode()) {
            EMIHelper.validateAmount(amountET2);
        } else if (currentInputHashCode == interestET1.getText().hashCode()) {
            EMIHelper.validateInterestRate(interestET1);
        } else if (currentInputHashCode == interestET2.getText().hashCode()) {
            EMIHelper.validateInterestRate(interestET2);
        } else if (currentInputHashCode == downPaymentET1.getText().hashCode()) {
            EMIHelper.validateDownPayment(downPaymentET1);
        } else if (currentInputHashCode == downPaymentET2.getText().hashCode()) {
            EMIHelper.validateDownPayment(downPaymentET2);
        } else if (currentInputHashCode == tenureET1.getText().hashCode()) {
            EMIHelper.validateTenure(tenureET1, isTenureInMonths);
        } else if (currentInputHashCode == tenureET2.getText().hashCode()) {
            EMIHelper.validateTenure(tenureET2, isTenureInMonths);
        }
    }

    public void resetFields() {
        EMIHelper.resetFields(amountET1, interestET1, downPaymentET1, tenureET1);
        EMIHelper.resetFields(amountET2, interestET2, downPaymentET2, tenureET2);
        tenureSpinner.setSelection(0); //months
        EMIHelper.hideKeyboard(getActivity());
        hideResultLayout();
    }

    public void hideResultLayout() {
        comparatorView.findViewById(R.id.compareResultHeading).setVisibility(View.INVISIBLE);
        comparatorView.findViewById(R.id.resultLayout).setVisibility(View.INVISIBLE);
        comparatorView.findViewById(R.id.resultFooter).setVisibility(View.INVISIBLE);
        comparatorView.findViewById(R.id.mainLayout).setBackgroundResource(R.color.main_background_color);
    }

    public void showResultLayout() {
        comparatorView.findViewById(R.id.compareResultHeading).setVisibility(View.VISIBLE);
        comparatorView.findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);
        comparatorView.findViewById(R.id.resultFooter).setVisibility(View.VISIBLE);
        comparatorView.findViewById(R.id.mainLayout).setBackgroundResource(R.color.main_background_post_color);
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

        if (validateInputs()) {
            //Loan1
            principalAmount1 = Long.parseLong(amountET1.getText().toString());
            interestRate1 = Float.parseFloat(interestET1.getText().toString());

            tenure1 = Integer.parseInt(tenureET1.getText().toString());
            tenure1 = isTenureInMonths ? tenure1 : tenure1 * 12;

            downPayment1 = ("").equals(downPaymentET1.getText().toString()) ? 0L :
                    Long.parseLong(downPaymentET1.getText().toString());

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
            tenure2 = isTenureInMonths ? tenure2 : tenure2 * 12;

            downPayment2 = ("").equals(downPaymentET2.getText().toString()) ? 0L :
                    Long.parseLong(downPaymentET2.getText().toString());

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
            showResultLayout();
        } else {
            hideResultLayout();
        }
    }

    public boolean validateInputs() {
        boolean valid = true;
        String alertMessage = "";

        if (amountET1.getError() != null || interestET1.getError() != null || downPaymentET1.getError() != null || tenureET1.getError() != null
                || amountET2.getError() != null || interestET2.getError() != null || downPaymentET2.getError() != null || tenureET2.getError() != null) {
            ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
            dialogFragment.setMessage(CommonConstants.ERROR_MESSAGE_ERRORS_PRESENT);
            dialogFragment.setTitle(CommonConstants.ERROR_TITLE_INVALID);
            dialogFragment.show(getFragmentManager(), "MyDialog");
            valid = false;
        } else {
            if (amountET1.getText().toString().equals("")) {
                alertMessage = alertMessage.concat(", ").concat(CommonConstants.PRINCIPAL_AMOUNT_1);
                valid = false;
            }
            if (amountET2.getText().toString().equals("")) {
                alertMessage = alertMessage.concat(", ").concat(CommonConstants.PRINCIPAL_AMOUNT_2);
                valid = false;
            }
            if (interestET1.getText().toString().equals("")) {
                alertMessage = alertMessage.concat(", ").concat(CommonConstants.INTEREST_RATE_1);
                valid = false;
            }
            if (interestET2.getText().toString().equals("")) {
                alertMessage = alertMessage.concat(", ").concat(CommonConstants.INTEREST_RATE_2);
                valid = false;
            }
            if (tenureET1.getText().toString().equals("")) {
                alertMessage = alertMessage.concat(", ").concat(CommonConstants.TENURE_1);
                valid = false;
            }
            if (tenureET2.getText().toString().equals("")) {
                alertMessage = alertMessage.concat(", ").concat(CommonConstants.TENURE_2);
                valid = false;
            }
            if (!valid) {
                alertMessage = EMIHelper.prettifyMessage(alertMessage);
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                dialogFragment.setMessage(alertMessage);
                dialogFragment.setTitle(CommonConstants.ERROR_TITLE_MISSING_INPUT);
                dialogFragment.show(getFragmentManager(), "MyDialog");
            } else {
                if ((!downPaymentET1.getText().toString().equals("") && Long.parseLong(downPaymentET1.getText().toString()) >= Long.parseLong(amountET1.getText().toString())) ||
                        (!downPaymentET2.getText().toString().equals("") && Long.parseLong(downPaymentET2.getText().toString()) >= Long.parseLong(amountET2.getText().toString()))) {
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
}
