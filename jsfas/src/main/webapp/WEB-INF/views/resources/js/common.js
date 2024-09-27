var ModalDialogWindow;
var ModalDialogInterval;

function ModalDialogMaintainFocus()
{
  try
  {
    if (ModalDialogWindow.opener.closed)
     {
        window.clearInterval(ModalDialogInterval);
	ModalDialogWindow.close();
     }
  }
  catch (everything) {   }
}
        
function ModalDialogShow(url,w,h)
 {
	if(ModalDialogWindow){
		if(!ModalDialogWindow.closed) ModalDialogWindow.close();
	}
   var args='width='+w+',height='+h+',left=100,top=100,toolbar=0,';
       args+='location=0,status=1,menubar=0,scrollbars=1,resizable=0';  

   ModalDialogWindow=window.open(url,"",args); 
   ModalDialogWindow.focus(); 
   ModalDialogInterval = window.setInterval("ModalDialogMaintainFocus()",5);
 }

function ModalDialogShowWithResize(url,w,h)
{
	if(ModalDialogWindow){
		if(!ModalDialogWindow.closed) ModalDialogWindow.close();
	}
  var args='width='+w+',height='+h+',left=100,top=100,toolbar=0,';
      args+='location=0,status=1,menubar=0,scrollbars=1,resizable=1';  

  ModalDialogWindow=window.open(url,"",args); 
  ModalDialogWindow.focus(); 
  ModalDialogInterval = window.setInterval("ModalDialogMaintainFocus()",5);
}

function onBodyUnload(){
	if(!ModalDialogWindow){
		return;
	}
	if(!ModalDialogWindow.closed){
		ModalDialogWindow.close();
	}
	return;
}

function isEmpty(i_str)    // replacing IsEmpty()
/*
	Input parameter: (String) i_str
	Return value: (Boolean)
	Self-written function called: nil
	Note: '', null return true; but '   ' (spaces) return false
*/
{
    return (i_str? false : true);
}

function isAlphabet(i_str)    // replacing IsAlphabet()
/*
	Input parameter: (String) i_str
	Return value: (Boolean)
	Self-written function called: nil
*/
{
    return (i_str? !/[^a-zA-Z]/.test(i_str) : false);
}

function isInteger(i_str)        // replacing IsDigit() & IsInteger()
/*
	Input parameter: (String) i_str
	Return value: (Boolean)
	Self-written function called: nil
*/
{
    return (i_str? !/\D/.test(i_str) : false);
}

function isAlphaNumeric(i_str)    // replacing IsAlphaNumeric()
/*
	Input parameter: (String) i_str
	Return value: (Boolean)
	Self-written function called: nil
*/
{
    return (i_str? !/[^a-zA-Z0-9]/.test(i_str) : false);
}

function isNumber(i_str)    // replacing IsNumber()
/*
	Input parameter: (String) i_str
	Return value: (Boolean)
	Self-written function called: trim()
*/
{
    return (trim(i_str)? !isNaN(i_str) : false);
}

function chk2Decimal(i_str)    // replacing Chk2Decimal()
/*
	Input parameter: (String) i_str, i_str is expected to be a valid number, call isNumber() to validate beforehand
	Return value: (Boolean)
	Self-written function called: nil
*/
{
    var arrayOfStrings = i_str.split('.');
    return (arrayOfStrings.length <= 2 && (!arrayOfStrings[1] || arrayOfStrings[1].length <= 2));
}

function isPatternMatch(i_str, i_pattern, i_fix)    // replacing ChkPattern() and ChkPattern_Ext()
/*
	Input parameter: (String) i_str
	                 (String) i_pattern, i.e. ^ for alphabet, * for digit, + for alphanumeric and / for mask pattern meaning
	                 (Boolean) i_fix, an optional parameter; default is true and represents fix length comparision
	Return value: (Boolean)
	Self-written function called: occurCount(), isEmpty(), isAlphabet(), isInteger(), isAlphaNumeric()
	Note: Precede the pattern character(i.e. ^, *, +) with / will remove the pattern meaning
          e.g. pattern ++/^ab matches 'f2^ab'
               pattern **//**//**** matches a date string '01/11/2001'
*/
{
    i_fix = (i_fix==null)? true : i_fix;
    var slashCnt = occurCount(i_pattern, '/') - occurCount(i_pattern, '//')

    if  (isEmpty(i_str) || isEmpty(i_pattern) ||
         (i_fix && i_str.length != (i_pattern.length - slashCnt)) ||
         (!i_fix && i_str.length > (i_pattern.length - slashCnt)))
    return false;

    for (var i=0, j=0; i < i_str.length; i++, j++){
        if  (i_pattern.charAt(j) == '/') {
            j += 1;
            if  (i_pattern.charAt(j) != i_str.charAt(i)) return false;
        }
        else {
            switch (i_pattern.charAt(j)){
            case '^' :
                if  (!isAlphabet(i_str.charAt(i))) return false;
                break;
            case '*' :
                if  (!isInteger(i_str.charAt(i))) return false;
                break;
            case '+' :
                if  (!isAlphaNumeric(i_str.charAt(i))) return false;
                break;
            default :
                if  (i_pattern.charAt(j) != i_str.charAt(i)) return false;
            }
        }
    }
    return true;
}

function isValidFileName(i_str)        // replacing ChkFileNamePattern()
/*
	Input parameter: (String) i_str
	Return value: (Boolean)
	Self-written function called: isEmpty()
	Note: valid file name must contains alphanumber characters only and no more than 1 '.'
*/
{
    if  (isEmpty(i_str)) return false;

    var arrayOfStrings = i_str.split('.');
    return (arrayOfStrings.length <= 2 && isAlphaNumeric(arrayOfStrings[0]) &&
            (!arrayOfStrings[1] || isAlphaNumeric(arrayOfStrings[1])));
}

function isDate(i_str, i_sep)
/*
	Input parameter: (String) i_str
	                 (String) i_sep, an optional parameter; default date separator '/-.'
	Return value: (Boolean)
	Self-written function called: trim(), isEmpty(), occurCount(), isPatternMatch()
	Note: i_sep can contain more than 1 arbitrary character, e.g. isDate('12/12/2001', '/-'),
          such than isDate function will validate the date string using '/' and '-' only
*/
/*
	Modification History
	--------------------
	Date: 11/4/2002
	Function: isDate(i_str, i_sep)
	Description: Enhancement, 1. input parameter i_sep default to '/-.' instead of '/' only
	                          2. allow multiple arbitrary characters in i_sep e.g. '/-'
*/
{
    /* valid format ddmmyy  &  ddmmyyyy
                    or d/m/yy where d & m can be single or double; yy can be yyyy as well  */
    i_str = trim(i_str);
    if  (isEmpty(i_str)) return false;

    i_sep = (i_sep? i_sep : '/-.');

    for (var i=0; i<i_sep.length-1; i++) {
        if (isDate(i_str, i_sep.substr(i, 1)))
           return true;
    }

    i_sep = i_sep.charAt(i_sep.length-1);
    var sepCount = occurCount(i_str, i_sep);
    if  (sepCount == 0)
        if  (i_str.length != 6 && i_str.length != 8)
            return false;
        else
            i_str = i_str.substr(0,2) + i_sep + i_str.substr(2,2) + i_sep + i_str.substring(4);
    else
        if  (sepCount != 2) return false;

    var arrayOfStrings = i_str.split(i_sep);
    if  ((!isPatternMatch(arrayOfStrings[0], '**', false)) ||
         (!isPatternMatch(arrayOfStrings[1], '**', false)) ||
         (arrayOfStrings[2].length != 2 && arrayOfStrings[2].length != 4) ||
         (!isPatternMatch(arrayOfStrings[2], '****', false)))
        return false;
    var day = arrayOfStrings[0]-0;
    var month = arrayOfStrings[1]-1;
    var year = arrayOfStrings[2]-0;

    if  (year < 100)
        (year < 70) ? year += 2000 : year += 1900;
    var newDate = new Date(year, month, day);
    return (day == newDate.getDate() && month == newDate.getMonth());
}

function isTime(i_str)
/*
	Input parameter: (String) i_str
	Return value: (Boolean)
	Self-written function called: trim(), isEmpty()
	Note: valid time format h:m:s where h, m & s can be single or double digit; and s is optional
*/
{
    i_str = trim(i_str);
    if  (isEmpty(i_str)) return false;

    var arrayOfStrings = i_str.split(':');
    if  (arrayOfStrings.length == 2)
        arrayOfStrings[2] = '00';
    if  (arrayOfStrings.length != 3 ||
         isEmpty(arrayOfStrings[0]) ||
         isEmpty(arrayOfStrings[1]) ||
         isEmpty(arrayOfStrings[2])) return false;

    var hh = arrayOfStrings[0]-0;
    var mm = arrayOfStrings[1]-0;
    var ss = arrayOfStrings[2]-0;
    var newDate = new Date("January 1, 2000 " + i_str);
    return (hh == newDate.getHours() && mm == newDate.getMinutes() && ss == newDate.getSeconds());
}

function isDateTime(i_str, i_sep)
/*
	Input parameter: (String) i_str
	                 (String) i_sep, an optional parameter; default date separator '/'
	Return value: (Boolean)
	Self-written function called: trim(), isEmpty()
	Note: valid date format: refer isDate() function
          valid time format: refer isTime() function
          date and time are separated by at least 1 space
*/
{
    i_str = trim(i_str);
    if  (isEmpty(i_str)) return false;

    for (var o_str = ''; o_str != i_str;  i_str = i_str.replace(/  /, ' '))
         o_str = i_str;

    var arrayOfStrings = i_str.split(' ');
    return (arrayOfStrings.length == 2 && isDate(arrayOfStrings[0], i_sep) && isTime(arrayOfStrings[1]));
}


/*///////////////////////////////////////////////////////////////////////////////////////////////////
String Utility Functions
---------------------------
Note: Thorough type checking against the string argument should be called beforehand.
      For example, before we call the toNumber(i_str) function, we should call the
      isNumber(i_str) to ensure the i_str argument is a number string.
///////////////////////////////////////////////////////////////////////////////////////////////////*/

function lTrim(i_str)
/*
	Input parameter: (String) i_str
	Return value: (String)
	Self-written function called: nil
*/
{
    return (i_str? i_str.replace(/^ +/,'') : i_str);
}

function rTrim(i_str)
/*
	Input parameter: (String) i_str
	Return value: (String)
	Self-written function called: nil
*/
{
    return (i_str? i_str.replace(/ +$/,'') : i_str);
}

function trim(i_str)        // replacing TrimString()
/*
	Input parameter: (String) i_str
	Return value: (String)
	Self-written function called: rTrim(), lTrim()
*/
{
    return rTrim(lTrim(i_str));
}

function lPad(i_str, i_pad_char, i_str_length)    // replacing LPadString()
/*
	Input parameter: (String) i_str
	                 (String) i_pad_char, character padding to i_str
	                 (Number) i_str_length, length of i_str after padding
	Return value: (String)
	Self-written function called: nil
*/
{
    i_str = (i_str? i_str : '');
    while (i_str.length < i_str_length)
          i_str = i_pad_char + i_str;
    return i_str;
}

function rPad(i_str, i_pad_char, i_str_length)    // replacing PadString()
/*
	Input parameter: (String) i_str
	                 (String) i_pad_char, character padding to i_str
	                 (Number) i_str_length, length of i_str after padding
	Return value: (String)
	Self-written function called: nil
*/
{
    i_str = (i_str? i_str : '');
    while (i_str.length < i_str_length)
          i_str += i_pad_char;
    return i_str;
}

function replaceString(i_str, i_text, i_new_text)    // replacing ReplaceString() and ReplaceAllString()
/*
	Input parameter: (String) i_str
	                 (String) i_text, text being replaced
	                 (String) i_new_text, replacing text
	Return value: (String)
	Self-written function called: nil
*/
{
    if  (i_str == null || i_text == null || i_new_text == null)
        return null;
    var arrayOfStrings = i_str.split(i_text);
    var newStr='';
    for (var i = 0; i<arrayOfStrings.length; i++)
        newStr = (i>0)? newStr + i_new_text + arrayOfStrings[i] : arrayOfStrings[i];
    return newStr;
}

function appendString(i_str, i_add, i_seperator)    // replacing AppendString()
/*
	Input parameter: (String) i_str
	                 (String) i_add, adding string
	                 (String) i_seperator, seperator between i_str and i_add
	Return value: (String)
	Self-written function called: nil
*/
{
    return (i_str? i_str + i_seperator + i_add : i_add);
}

function occurCount(i_str, i_text)
/*
	Input parameter: (String) i_str
	                 (String) i_text, text being counted the occurance
	Return value: (Number)
	Self-written function called: nil
*/
{
    return (i_str.split(i_text).length - 1);
}

function padDecimal(i_str, i_dec)        // replacing Pad2Decimal();  i_str normally must be a number
/*
	Input parameter: (String) i_str
	                 (Number) i_dec, an optional parameter (defaults to 0), the number of decimal place to be padded
	Return value: (String)
	Self-written function called: toNumber(), decPlace()
*/
{
    i_dec = (i_dec? i_dec : 0);
    var num = toNumber(i_str);
    var dec = decPlace(num);
    var numStr = String(num) + ((dec==0 && i_dec>0)? '.' : '');
    for (var i=i_dec-dec; i>0; i--)
         numStr += '0';
    return (numStr.charAt(0)=='.'? '0' : '') + numStr;
}


/*///////////////////////////////////////////////////////////////////////////////////////////////////
String Conversion Functions
---------------------------
///////////////////////////////////////////////////////////////////////////////////////////////////*/

function toNumber(i_str)
/*
	Input parameter: (String) i_str
	Return value: (Number)
	Self-written function called: nil
	Note: this function is better than eval(i_str) because eval('004') will cause an error on leading zero number
*/
{
    return Number(i_str).valueOf();
}

function toDate(i_str, i_sep)
/*
	Input parameter: (String) i_str, must be a valid date string; should call isDate() to validate beforehand
	                 (String) i_sep, an optional parameter; default date separator '/-.'
	Return value: (Date)
	Self-written function called: trim()
	Note: i_sep can contain more than 1 arbitrary character, e.g. isDate('12/12/2001', '/-'),
          such than toDate function will convert the date string using '/' and '-' only
*/
/*
	Modification History
	--------------------
	Date: 11/4/2002
	Function: toDate(i_str, i_sep)
	Description: Enhancement, 1. input parameter i_sep default to '/-.' instead of '/' only
	                          2. allow multiple arbitrary characters in i_sep e.g. '/-'
*/{
    i_str = trim(i_str);
    i_sep = (i_sep? i_sep : '/-.');

    for (;i_sep.length > 1;) {
        if  (i_str.indexOf(i_sep.substr(0,1)) >= 0)
             break;
        i_sep = i_sep.substr(1);
    }

    i_sep = i_sep.substr(0,1);

    if  (i_str.indexOf(i_sep) < 0)
        i_str = i_str.substr(0,2) + i_sep + i_str.substr(2,2) + i_sep + i_str.substring(4);

    var arrayOfStrings = i_str.split(i_sep);
    var day = arrayOfStrings[0]-0;
    var month = arrayOfStrings[1]-1;
    var year = arrayOfStrings[2]-0;

    if  (year < 100)
        (year < 70) ? year += 2000 : year += 1900;
    return new Date(year, month, day);
}

function toDateTime(i_str, i_sep)
/*
	Input parameter: (String) i_str, must be a valid date string; should call isDateTime() to validate beforehand
	                 (String) i_sep, an optional parameter for date conversion, see toDate for default value
	Return value: (Date)
	Self-written function called: trim()
	Note: date and time are separated by at least 1 space
*/
{
    i_str = trim(i_str);
    for (var o_str = ''; o_str != i_str;  i_str = i_str.replace(/  /, ' '))
         o_str = i_str;

    var arrayOfStrings = i_str.split(' ');
    newDate = toDate(arrayOfStrings[0], i_sep);

    var arrayOfTime = arrayOfStrings[1].split(':');
    if  (arrayOfTime.length == 2)
        arrayOfTime[2] = '00';

    var hh = arrayOfStrings[0]-0;
    var mm = arrayOfStrings[1]-0;
    var ss = arrayOfStrings[2]-0;
    var dummyDate = new Date("January 1, 2000 " + arrayOfStrings[1]);
    newDate.setHours(dummyDate.getHours());
    newDate.setMinutes(dummyDate.getMinutes());
    newDate.setSeconds(dummyDate.getSeconds());
    return newDate;
}

function escapeStr(i_str)
/*
    Similar to escape() - but do more on special characters
    Input parameter: (String) i_str
    Return value: the value with special characters escaped
    Self-written function called: escape(), replaceString()
    Note: escape() is a JavaScript built-in function, similar to
          url-encode() in WebSpeed, however, some of the special
          characters would not be encoded, they are:
          >>> * @ - _ + . /  <<<
          (Re: Client-Side JavaScript Reference v1.2)
          Some of the above characters passed in URL, and use
          get-value(), the value would be changed, such as the
          character +, it would be changed to a space. So,
          this function takes care of these special characters.
          Notice that after URL encode, the characters: * - . _
          would remains unchanged, so no need to cater for these.
*/
{
    i_str = escape(i_str);
    i_str = replaceString(i_str, '@', '%40');
    i_str = replaceString(i_str, '+', '%2B');
    i_str = replaceString(i_str, '/', '%2F');
    return i_str;
}

/*///////////////////////////////////////////////////////////////////////////////////////////////////
Number Functions
---------------------------
///////////////////////////////////////////////////////////////////////////////////////////////////*/

function decPlace(i_num)
/*
	Input parameter: (Number) i_num
	Return value: (Number)
	Self-written function called: nil
	Example: decPlace(-123456.789) returns 3
*/
{
    i_num += '';
    var dec = i_num.indexOf('.');
    return (dec < 0)? 0 : i_num.length - dec - 1;
}

function roundNum(i_num, i_dec)
/*
	Input parameter: (Number) i_num
	                 (Number) i_dec, decimal point; default 0 decimal
	Return value: (Number)
	Self-written function called: nil
	Example: e.g. roundNum(-123456.789, 2) returns 123456.79
                  roundNum(-123456.789, -2) returns -123500
*/
{
    i_dec = (i_dec? i_dec : 0);
    return Math.round(i_num*Math.pow(10,i_dec))/Math.pow(10,i_dec);
}

function formatNum(i_num, i_dec)
/*
     Input parameter: (Number) i_num
                      (Number) i_dec, decimal point; default 0 decimal
     Return value: (String)
     Self-written function called: roundNum()
     Example: e.g. formatNum(-123456.789, 2) returns "-123,456.79"
*/
{
     i_dec = (i_dec? i_dec : 0);
     var neg = i_num < 0;
     var arrayOfStrings = (roundNum(Math.abs(i_num), i_dec) + '').split('.');

     var o_decNum = '.';
     /*   Bug found when input number is an integer;
          Previously, arrayOfStrings[1] of an integer value contains a blank, instead of undefined
     for (var i = 0; i < i_dec; i++)
         o_decNum += arrayOfStrings[1].charAt(i);
     if  (o_decNum.length == 1) o_decNum = '';
     */
     /* bug fixing */
     if (arrayOfStrings.length == 1)
        o_decNum = '';
     else
        for (var i = 0; i < i_dec; i++)
            o_decNum += arrayOfStrings[1].charAt(i);
     /* end of bug fixing */

     var o_digNum = '';
     if  (arrayOfStrings[0].length < 4)
         o_digNum = arrayOfStrings[0];
     else {
         var mod = arrayOfStrings[0].length%3;
         o_digNum = (mod > 0 ? (arrayOfStrings[0].substring(0,mod)) : '');
         for (i=0; i < Math.floor(arrayOfStrings[0].length/3) ; i++) {
             if ((mod ==0) && (i ==0))
                o_digNum += arrayOfStrings[0].substring(mod+3*i,mod+3*i+3);
             else
                o_digNum += ',' +
arrayOfStrings[0].substring(mod+3*i,mod+3*i+3);
         }
     }
     if  (neg) o_digNum = '-' + o_digNum;
     return (o_digNum + o_decNum);
}


/*///////////////////////////////////////////////////////////////////////////////////////////////////
Date/Time Utility Functions
------------------------------
///////////////////////////////////////////////////////////////////////////////////////////////////*/

function daysElapsed(i_date1, i_date2)
/*
	Input parameter: (Date) i_date1
	                 (Date) i_date2
	Return value: (Number)
	Self-written function called: nil
	Note: i_date1 and i_date2 must be dates, partial day is counted as 1 day
*/
{
    var days = (i_date2 - i_date1)/1000/60/60/24;
    return (days > 0)? Math.ceil(days) : Math.floor(days);
}

function pause(i_milliseconds)
/*
	Input parameter: (Number) i_milliseconds, milliseconds to be paused
	Return value: nil
	Self-written function called: nil
*/
{
    var startTime = new Date();
    while ((new Date()).getTime() < startTime.getTime() + i_milliseconds);
}

function y2k(i_year)
/*
	Input parameter: (Number) i_year
	Return value: (Number)
	Self-written function called: nil
*/
{
    return (i_year < 1000 ? i_year + 1900 : i_year);
}

function monthEndDate(i_date)
/*
	Input parameter: (Date) i_date
	Return value: (Date)
	Self-written function called: addDays(), y2k()
*/
{
    var myDate = addDays(new Date(y2k(i_date.getYear()), i_date.getMonth(), 1), 31);  // get next month
    myDate.setDate(1);
    myDate.setTime(myDate.getTime() - 1);  // get month end
    return new Date(y2k(myDate.getYear()), myDate.getMonth(), myDate.getDate());
}

function addDays(i_date, i_days)
/*
	Input parameter: (Date) i_date
	                 (Number) i_days, number of days to be added to i_date
	Return value: (Date)
	Self-written function called: nil
	Note: i_days can also be -ve
*/
{
    return new Date(i_date.getTime() + i_days*24*60*60*1000);
}

function addMonths(i_date, i_months)
/*
	Input parameter: (Date) i_date
	                 (Number) i_months, number of months to be added to i_date
	Return value: (Date)
	Self-written function called: y2k(), monthEndDate()
	Note: i_months can also be -ve
*/
{
    var dd = i_date.getDate();
    var total_mm = y2k(i_date.getYear()) * 12 + i_date.getMonth() + i_months;
    var myDate = monthEndDate(new Date(Math.floor(total_mm / 12), total_mm % 12, 1));
    if (dd < myDate.getDate())
        myDate.setDate(dd);
    return myDate;
}

function formatDate(i_date, i_separator)
/*
	Input parameter: (Date) i_date
	                 (String) i_separator, an optional date separator; default '/'
	Return value: (String)
	Self-written function called: lPad(), y2k()
	Note: result is in dd/mm/yyyy format
*/
{
    i_separator = (i_separator? i_separator : '/');
    return(lPad(String(i_date.getDate()), '0', 2) + i_separator + lPad(String(i_date.getMonth() + 1), '0', 2) + i_separator + y2k(i_date.getYear()));
}

function formatTime(i_date)
/*
	Input parameter: (Date) i_date
	Return value: (String)
	Self-written function called: lPad()
	Note: result is in hh:mm:ss format
*/
{
    return(lPad(String(i_date.getHours()), '0', 2) + ':' + lPad(String(i_date.getMinutes()), '0', 2) + ':' + lPad(String(i_date.getSeconds()), '0', 2));
}

function formatShortTime(i_date)
/*
	Input parameter: (Date) i_date
	Return value: (String)
	Self-written function called: lPad()
	Note: result is in hh:mm format
*/
{
    return(lPad(String(i_date.getHours()), '0', 2) + ':' + lPad(String(i_date.getMinutes()), '0', 2));
}


/*///////////////////////////////////////////////////////////////////////////////////////////////////
HTML Form field Utility Functions
---------------------------------
///////////////////////////////////////////////////////////////////////////////////////////////////*/

function radioValue(i_radio_fld)
/*
	Input parameter: (Radio) i_radio_fld, Radio form field
	Return value: (String)
	Self-written function called: nil
*/
/*
	Modification History
	--------------------
	Date: 4/1/2002
	Function: radioValue(i_radio_fld)
	Description: Bug fixing, input argument name should be i_radio_fld (instead of i_radio_field)
*/
{
    for (var i = 0; i < i_radio_fld.length ; i++)
        if  (i_radio_fld[i].checked) return i_radio_fld[i].value;
    return null;
}

function chkRefreshTextarea(i_textarea, i_lineLen, i_skipBlankLine, i_maxLen)
/*
	Input parameter: (Object) i_textarea, the input textarea
	                 (Number) i_lineLen, the length of character each split text will hold
	                 (Boolean) i_skipBlankLine, indicate skip or not the blank line when splitting the text
	                 (Number) i_maxLen, the max length of escaped textarea
    Return value: (String) 'n'  = a string represent the no. of lines entered
                           'E1' = input > system limit of i_maxLen characters
                           'E2' = input contains reserved token "_*"
	Self-written function called: replaceString()

    Example: chkRefreshTextarea(document.forms[0].textarea1, 40, false, 18000);
    Usage Note: 1. Used in textarea of defined no. of lines, e.g. address field
                   - i_maxLen parameter of 18000 char is good enough
                   - programmer should validate the return value for no. of lines entered
                2. Used in textarea of dynamic no. of lines, e.g. order item description
                   - i_maxLen parameter must be taken into consideration
                   - normally, 18000 char is good enough for 300 ~ 400 lines of 40 char/line
                   - in case a bigger sized textarea is expected, a bigger i_maxLen has to be specifed;
                     which in turn requires webspeed broker parameter -s to be increased
                   - therefore, using this js function with dynamic sized textarea has to be careful, and
                     consult with ISO system team if necessary
*/
{
    i_str = i_textarea.value;	// new code

    token = '_*';        /* default token value "_*",
                            use "_*" instead of "^*" because "_" will not be escaped by escape() javascript function */
    if  (i_str.indexOf(token) >= 0)  return 'E2';		// error: input string cannot contains any token

    i_str = replaceString(i_str, '\r\n', token);        // N4+ and IE use \r\n for carriage return in pc
    i_str = replaceString(i_str, '\n', token);        // N6+ use \n for carriage return in pc
    i_str = replaceString(i_str, '\r', token);        // N4+ use \r for carriage return in mac

    // re-construct i_str by chopping line with length > i_lineLen into multiple lines
    arrayOfStrings = i_str.split(token);
    for (var i=0; i < arrayOfStrings.length; i++) {
        currStr=''; tempStr='';
        while (arrayOfStrings[i].length > i_lineLen) {
            tempStr = arrayOfStrings[i].substring(0, i_lineLen);
            lineLen = Math.min(tempStr.lastIndexOf(' '), i_lineLen);
            lineLen = (lineLen == -1)? i_lineLen : lineLen;
            tempStr = tempStr.substring(0, lineLen + 1);
            currStr += (currStr)? token + tempStr : tempStr;
            arrayOfStrings[i] = arrayOfStrings[i].substring(tempStr.length);
        }
        currStr += currStr? token + arrayOfStrings[i] : arrayOfStrings[i];
        i_str = (i==0)? currStr : i_str + token + currStr;
    }

    if  (i_skipBlankLine) {
        // format token regular expression
        var tokenPattern = '';
        for (var i=0; i<token.length; i++)
            tokenPattern += '\\' + token.charAt(i);
        re = new RegExp(tokenPattern + ' *' + tokenPattern, 'g');

        // replace all "token token" with a single "token"
        for (var old_str = ''; old_str != i_str; i_str = i_str.replace(re, token))
            old_str = i_str;

  		// trim leading & trailing "token"
        i_str = i_str.replace(new RegExp('^ *' + tokenPattern), '').replace(new RegExp(tokenPattern + ' *$'), '');
    }

    // new code
    if  (escapeStr(i_str).length > i_maxLen) return 'E1';    // error: input > system limit of i_maxLen characters

    // fill up fields with i_str
    arrayOfStrings = i_str.split(token);

    // new code
    i_textarea.value = '';
    for (var i=0; i < arrayOfStrings.length; i++)
        i_textarea.value += (i>0? '\r':'') + arrayOfStrings[i];

    return arrayOfStrings.length + '';	// success
}

function text2Fields(i_str, i_fieldPrefix, i_fieldCnt, i_lineLen, i_skipBlankLine)
/*
	Input parameter: (String) i_str, string from textarea
	                 (String) i_fieldPrefix, field to be assigned the split text eg. "document.forms[0].fd_rem_"
	                 (Number) i_fieldCnt, the number of fields to hold the split text
	                 (Number) i_lineLen, the length of character each split text will hold
	                 (Boolean) i_skipBlankLine, indicate skip or not the blank line when splitting the text
    Return value: (String) 'n'  = a string represent the no. of lines entered
                           'E1' = input > i_fieldCnt
                           'E2' = input contains reserved token "^*"
	Self-written function called: replaceString()

    Example: text2Fields(this.value, "document.forms[0].fd_rem_", 5, 40, true);
             document.forms[0].fd_rem_1.value, document.forms[0].fd_rem_2.value, ...  document.forms[0].fd_rem_5.value
             will be set accordingly
*/
{
    token = '^*';        // default token value "^*"
    if  (i_str.indexOf(token) >= 0)  return 'E2';		// error: input string cannot contains any token

    i_str = replaceString(i_str, '\r\n', token);        // N4+ and IE use \r\n for carriage return in pc
    i_str = replaceString(i_str, '\n', token);        // N6+ use \n for carriage return in pc
    i_str = replaceString(i_str, '\r', token);        // N4+ use \r for carriage return in mac

    // re-construct i_str by chopping line with length > i_lineLen into multiple lines
    arrayOfStrings = i_str.split(token);
    for (var i=0; i < arrayOfStrings.length; i++) {
        currStr=''; tempStr='';
        while (arrayOfStrings[i].length > i_lineLen) {
            tempStr = arrayOfStrings[i].substring(0, i_lineLen);
            lineLen = Math.min(tempStr.lastIndexOf(' '), i_lineLen);
            lineLen = (lineLen == -1)? i_lineLen : lineLen;
            tempStr = tempStr.substring(0, lineLen + 1);
            currStr += (currStr)? token + tempStr : tempStr;
            arrayOfStrings[i] = arrayOfStrings[i].substring(tempStr.length);
        }
        currStr += currStr? token + arrayOfStrings[i] : arrayOfStrings[i];
        i_str = (i==0)? currStr : i_str + token + currStr;
    }

    if  (i_skipBlankLine) {
        // format token regular expression
        var tokenPattern = '';
        for (var i=0; i<token.length; i++)
        tokenPattern += '\\' + token.charAt(i);
        re = new RegExp(tokenPattern + ' *' + tokenPattern, 'g');

        // replace all "token token" with a single "token"
        for (var old_str = ''; old_str != i_str; i_str = i_str.replace(re, token))
            old_str = i_str;

  		// trim leading & trailing "token"
        i_str = i_str.replace(new RegExp('^ *' + tokenPattern), '').replace(new RegExp(tokenPattern + ' *$'), '');
    }

    // fill up fields with i_str
    arrayOfStrings = i_str.split(token);
    if  (arrayOfStrings.length > i_fieldCnt) return 'E1';    // error: input > i_fieldCnt

    for (var i=0; i < arrayOfStrings.length; i++)
        eval(i_fieldPrefix + (i + 1) + '.value = arrayOfStrings[i]');
    for (var i=arrayOfStrings.length; i < i_fieldCnt; i++)
        eval(i_fieldPrefix + (i + 1) + '.value = ""');
    return arrayOfStrings.length + '';	// success
}


function fields2Text(i_fieldPrefix, i_fieldCnt)
/*
	Input parameter: (String) i_fieldPrefix, field to be assigned the split text eg. "document.forms[0].fd_rem_"
	                 (Number) i_fieldCnt, the number of fields to hold the split text
    Return value: (String)
    Self-written function called: nil

    Example: document.forms[0].f_address.value = fields2Text('document.forms[0].f_add_line_', 4)
*/
{
    myStr = '';
    token = '\r\n';
    for (var i=1; i <= i_fieldCnt; i++)
        myStr += ((i > 1)? token : '') + eval(i_fieldPrefix + i + '.value');
    for (i=myStr.length-2; i>=0 && myStr.substring(i)==token; i-=2)
         myStr = myStr.substring(0,i);
    return myStr;
}

function getCookie(i_name) 
/*
        Input parameter: (String) i_name, cookie name 
        Return value: (String) - cookie value
        Self-written function called: nil

*/
{
      var start = document.cookie.indexOf(i_name+"=");
      var len = start+i_name.length+1;
      if ((!start) && (i_name != document.cookie.substring(0,i_name.length))) return null;
      if (start == -1) return null;
      var end = document.cookie.indexOf(";",len);
      if (end == -1) end = document.cookie.length;
      return unescape(document.cookie.substring(len,end));
}

function setCookie(i_name,i_value,i_expires,i_path,i_domain,i_secure) 
/*
        Input parameter: (String) i_name, cookie name
                         (String) i_value, cookie value
                         (String) i_expires, a date string that defines the valid life time of that cookie (normally no need to specify)
                         (String) i_path, path attributes for a valid cookie (normally no need to specify)
                         (String) i_domain, domain attributes for a valid cookie (normally no need to specify)
                         (String) i_secure, specifies that the cookie is transmitted only if the communications channel with the host is a secure (normally no need to specify)
        Return value: nil 
        Self-written function called: nil

        Note : for more information on attributes of cookies, pls refer to JavaScript Reference on cookies

        Example : setCookie("or_req_151028_flow_ctrl", "disable")
*/
{
      document.cookie = i_name + "=" +escape(i_value) +
          ( (i_expires) ? ";expires=" + i_expires.toGMTString() : "") +
          ( (i_path) ? ";path=" + i_path : "") +
          ( (i_domain) ? ";domain=" + i_domain : "") +
          ( (i_secure) ? ";secure" : "");
}


function getFlowCtrlCookie(i_cookie_entry_name) 
/*
        Input parameter: (String) i_cookie_entry_name
        Return value: (String) - enable / disable
        Self-written function called: getCookie

*/
{
      cookie_value = getCookie('page$flow$ctrl'); 
      if (cookie_value == null) return ('enable');
      if (cookie_value.indexOf(i_cookie_entry_name + '|') == -1)
        return ('disable');
      else
        return ('enable');        
}


function disableFlowCtrlCookie(i_cookie_entry_name) 
/*
        Input parameter: (String) i_cookie_entry_name
        Return value: nil
        Self-written function called: getCookie, setCookie

*/
{
      cookie_value = getCookie('page$flow$ctrl');

      if (cookie_value != null) {
        tmp_idx = cookie_value.indexOf(i_cookie_entry_name + '|');
        if (tmp_idx != -1) {
	  cookie_value = cookie_value.substr(0,tmp_idx) + 
                         cookie_value.substr(tmp_idx + i_cookie_entry_name.length + 1);
          if (cookie_value == null) 
            cookie_value = '*'; 
          else 
            if (trim(cookie_value) == '')
              cookie_value = '*'; 
          setCookie('page$flow$ctrl', cookie_value); 
        }     
      }
}

function checkDate(input_id, input_id_desc){
	input_date = $(input_id).val();
	input_date = trim(input_date);
	alert(input_date);
	if(input_date == "") return true;
	if(!isDate(input_date)){
		alert(input_id_desc + " is not a valid date format of dd/mm/yyyy.");
		return false;
	}
	return true;
}

/**
 * addAlert function to simplify calling the bootstrap alert
 *
 * @param style
 *    alert color style (bootstrap color style): default, primary, success, info, danger
 * @param alertBody
 *    message or html body of alert
 * @param autoFadeOut
 *    auto fade out the alert: true or false, default: false
 */
function addAlert(style, alertBody, autoFadeOut) {
	alertHtml = "<div class=\"alert alert-" + style  + " alert-dismissible\" role=\"alert\" style=\"display:none\">" + 
		"<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>" + 
		alertBody + "</div>";
	alertElement = $(alertHtml);
	alertElement.on('closed.bs.alert', function () {
		$("#alertBoxWrapper").height($("#alertBox").outerHeight());
	});
	alertElement.appendTo($("#alertBox"));
	
	if (autoFadeOut) {
		alertElement.fadeIn(1000).delay(2000).fadeOut(1500, function() {
			$(this).alert('close');
		});
	} else {
		alertElement.fadeIn(1000);
	}
	
	$("#alertBoxWrapper").height($("#alertBox").outerHeight());
	var stickyOffset = $("#alertBox").offset().top - 62;
	if ($(window).scrollTop() >= stickyOffset) {
		$(window).scrollTop($(window).scrollTop() + 72);
	}
}

/**
 * clear bootstrap alert
 *
 * @param style
 *    alert color style (bootstrap color style): default, primary, success, info, danger
 *    or all for any type of alert
 */
function clearAlert(style) {
	if (style.toUpperCase() == "ALL") {
		$("#alertBox").find(".alert").each(function() {
			$(this).remove();
		});
	} else {
		$("#alertBox").find(".alert-" + style ).each(function() {
			$(this).remove();
		});
	}
	
	$("#alertBoxWrapper").height($("#alertBox").outerHeight());
	var stickyOffset = $("#alertBox").offset().top - 62;
	if ($(window).scrollTop() >= stickyOffset) {
		$(window).scrollTop($(window).scrollTop() + 72);
	}
}

/**
 * active the select all checkbox for datatable
 * @param dataTableObj
 * @param totalSelectableRowsCount
 */
function activeSelectAllCheckbox(dataTableObj, totalSelectableRowsCount) {
	//check the select all button if total selected rows = total rows
	dataTableObj.on('select.dtSelect.dt', function(e, dt, type, indexes) {
		if (dataTableObj.rows({selected: true}).flatten().length == totalSelectableRowsCount) {
			//$("#exampleResultSet th.select-checkbox").parent().addClass("selected");
			$(this).find("th.select-checkbox").parent().addClass("selected");
		}
	});
	
	//reset select all checkbox if total selected rows < total rows
	dataTableObj.on('deselect.dtSelect.dt', function(e, dt, type, indexes) {
		if (dataTableObj.rows({selected: true}).flatten().length < totalSelectableRowsCount) {
			//$("#exampleResultSet th.select-checkbox").parent().removeClass("selected");
			$(this).find("th.select-checkbox").parent().removeClass("selected");
		}
	});
	
	dataTableObj.nodes().to$().find("th.select-checkbox").click(function() {
		if ($(this).parent().hasClass("selected")) {
			dataTableObj.rows().deselect();
			$(this).parents().removeClass("selected");
		} else {
			dataTableObj.rows().select();
			$(this).parent().addClass("selected");
		}
	});
}

/**
 * refresh page
 */
function refreshPage() {
	location.reload(true);
}

/**
 * redirect page
 */
function redirectPage(url) {
	window.location.replace(url);
}

function showSuccess(message) {
	$('#successMsg').html(message);
	$('#successModal').modal({
		backdrop: 'static',
		keyboard: false
	});
}

/**
 * Common Ajax function for integrated common behavior in Ajax call and error handling
 * <br>version 2.0
 * 
 * @param {JSON} inputJson - the inputJson object (parameters) (mandatory)
 * @param {String} apiPage - the ajax api call page (mandatory)
 * @param {Function} successFn - the function() after api return success (200) status (mandatory)
 * @param {Function} formDisableFn - the function() when api return record not found, modified or removed or  (402, 403, 404) status (optional)
 * @param {Function} formErrorFn - the function() when api return unexpected error or unauthorized error (optional)
 * @param {String} redirectPage the - url that "Ok" and "Return" buttons click (optional)
 * @param {Boolean} renderLoadingSpin - flag for enable (default) render loading spin (optional)
 * @param {JSON} config - the config which replace the original Ajax call setting (optional)
 * 
 * @return {jqXHR} jqXHR Object, which is a superset of the XMLHTTPRequest object
 * 
 * @author iswill
 */
function commonAjax(inputJson, apiPage, successFn, formDisableFn, formErrorFn, redirectPage, renderLoadingSpin, config) {
	var defaultConfig = undefined;
	var ajaxConfig = undefined;
	
	if(renderLoadingSpin === undefined) {
		renderLoadingSpin = true;
	}
	
	defaultConfig = {
		beforeSend: function(xhr) {
			xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
		},
		url: apiPage,
		type: "post",
		dataType: "json",
		contentType: "application/json; charset=utf-8",
		data: JSON.stringify(inputJson),
		success: function (response) {
			if (response.status=="200") {
				successFn && successFn(response);
			} else if (response.status == "402") {
				formDisableFn && formDisableFn(response);
				addAlert("danger", "Error: " + response.message 
						+ "<br><br><button type=\"button\" onclick=\"refreshPage();\" class=\"btn btn-sm btn-danger\">"
						+ "<span class=\"glyphicon glyphicon-refresh\"></span> Retry</button>");
			} else if (response.status == "403") {
				formDisableFn && formDisableFn(response);
				addAlert("danger", "Error: " + response.message 
						+ "<br><br><button type=\"button\" onclick=\"refreshPage();\" class=\"btn btn-sm btn-danger\">"
						+ "<span class=\"glyphicon glyphicon-refresh\"></span> Retry</button>");
			} else if (response.status == "404") {
				formDisableFn && formDisableFn(response);
				addAlert("danger", "Error: " + response.message 
						+ "<br><br><button type=\"button\" onclick=\"redirectPage('"+ redirectPage +"');\" class=\"btn btn-sm btn-danger\">"
						+ "<span class=\"glyphicon glyphicon-chevron-left\"></span> Return</button>");
			} else {
				formErrorFn && formErrorFn(response);
				addAlert("danger", "Error: " + response.message);
			}
			
			renderLoadingSpin && $('#loadingSpin').hide();
		},    
		error: function(xhr, textStatus, error) {
			if (xhr.responseText && xhr.responseText.indexOf("Access Denied!") != -1) {
				refreshPage();
				return false;
			}
			if (xhr.responseText && xhr.responseText.indexOf("Suspected malicious") != -1) {
				$("html").html(xhr.responseText);
				return false;
			}
			//handle the if error before browser post
			if (xhr.status && (xhr.status === 0 || xhr.status === 1 || xhr.status === 2 || xhr.status === 3 || xhr.status === 4)) {
                addAlert("danger", "Error: Ajax error" 
						+ "<br><br><button type=\"button\" onclick=\"refreshPage();\" class=\"btn btn-sm btn-danger\">"
						+ "<span class=\"glyphicon glyphicon-refresh\"></span> Retry</button>");
                renderLoadingSpin && $('#loadingSpin').hide();
                return false;
			}
			window.location.href = $("meta[name='_cas_expire_url']").attr("content");
			renderLoadingSpin && $('#loadingSpin').hide();
		}	
	};
		
	ajaxConfig = $.extend(true, {}, defaultConfig, config);
	
	if (typeof ($._data($("#okBtn")[0], 'events')) === 'undefined' || typeof ($._data($("#okBtn")[0], 'events').click) === 'undefined') {
		$("#okBtn").click(function() {
			window.location.replace(redirectPage);
		});
	};
	
	renderLoadingSpin && $('#loadingSpin').show();
	clearAlert("all");
	return $.ajax(ajaxConfig);
}