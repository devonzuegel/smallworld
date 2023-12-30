
// Manages the phone numbre input
const displayPhoneInput = document.getElementById("display-phone");
const hiddenPhoneInput = document.getElementById("phone");

console.log('phone input', displayPhoneInput, hiddenPhoneInput);

if (displayPhoneInput && hiddenPhoneInput) {
  console.log('in');
  const iti = window.intlTelInput(displayPhoneInput, {
    separateDialCode: true,
    initialCountry: "us",
    utilsScript:
      "https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js",
  });

  displayPhoneInput.addEventListener('change', function(event) {
    console.log('selected country data',  iti.getSelectedCountryData());
    var formattedNumber = iti.getNumber(intlTelInputUtils.numberFormat.E164);
    console.log('original number', event.target.value);
    console.log('formatted number', formattedNumber);
    hiddenPhoneInput.value = formattedNumber;
  });
}

