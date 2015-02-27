package studio.idle.emicalculator.common;

/**
 * Created by ujain on 2/21/15.
 */
public  class EMIHelper {

    public static Double calculateEMI(Long principalAmount, float rateOfInterest, Long downPayment, int tenureInMonths) {
        Double emi=((principalAmount - downPayment) * (rateOfInterest/(12*100)))*((Math.pow((1+(rateOfInterest/(12*100))),tenureInMonths)))/((Math.pow((1+(rateOfInterest/(12*100))),tenureInMonths)-1));
        return emi;
    }

    public static String constructShareText(Long principalAmount, float interestRate, Long downPayment, int tenure, boolean isTenureInMonths, Long emiRounded, Long totalInterest, Long totalAmountPayable) {

        String tenureType = "";
        tenureType = isTenureInMonths ? "Months": "Years";
        String shareText = "Inputs: \n\n";
        shareText += "Loan Amount :" + (principalAmount - downPayment) + "\n" + "Rate of Interest(/year) : " + interestRate + "\n" + "Tenure : " + tenure + " " + tenureType + "\n\n";
        shareText += "EMI Calculation: \n\n";
        shareText += "EMI Amount: " + emiRounded + "\n" + "Total Interest Payable: " + totalInterest + "\n" + "Total Amount Payable: " + totalAmountPayable + "\n\n";
        return shareText;
    }
}
