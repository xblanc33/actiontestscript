var parent = arguments[0].parentNode, result = [];

while (parent != null){
	addElement(parent);
	
	if(parent.nodeName == 'BODY'){
		parent = parent.ownerDocument.defaultView.frameElement;
		while(parent != null){
			addElement(parent);
			parent = parent.ownerDocument.defaultView.frameElement;
		}
	}else{
		parent = parent.parentElement;
	}
};

function addElement(e){
	try{
		let rec = e.getBoundingClientRect();
		result[result.length] = [e, e.tagName, rec.width+0.0001, rec.height+0.0001, rec.left+0.0001, rec.top+0.0001];
	}catch(error){
		result[result.length] = [e, '', 0.0001, 0.0001, 0.0001, 0.0001];
	}
}