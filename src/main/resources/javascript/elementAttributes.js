var element = arguments[0];
var result = [];
var attrib, textValue;

for(var i=0, len=element.attributes.length; i<len; i++){
	attrib = element.attributes[i];
	if(attrib.name != 'value'){
		result.push([attrib.name, attrib.value]);
	}
};

textValue = element.innerText || element.textContent;
if(textValue){
	textValue = textValue.trim();
	textValue = textValue.replace(/\xA0/g," ");
	textValue = textValue.replace(/\s+/g," ");
	result.push(['text', textValue]);
}

if(element.value){
	result.push(['value', element.value]);
};