
// Manages the phone numbre input
const displayPhoneInput = document.getElementById("display-phone");
const hiddenPhoneInput = document.getElementById("phone");

console.log('phone input', displayPhoneInput, hiddenPhoneInput);

if (displayPhoneInput && hiddenPhoneInput) {
  const iti = window.intlTelInput(displayPhoneInput, {
    separateDialCode: true,
    initialCountry: "us",
    utilsScript:
      "https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js",
  });

  displayPhoneInput.addEventListener('change', function(event) {
    var formattedNumber = iti.getNumber(intlTelInputUtils.numberFormat.E164);
    hiddenPhoneInput.value = formattedNumber;
  });
}

