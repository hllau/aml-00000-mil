if (typeof(cleanWhitespace)!="function")
{
	function cleanWhitespace(node)
	{
		if (typeof node.childNodes != 'undefined') {
			notWhitespace = /\S/;
			for (var x = 0; x < node.childNodes.length; x++)
			{
				var childNode = node.childNodes[x];
				if ((childNode.nodeType == 3)&&(!notWhitespace.test(childNode.nodeValue)))
				{
					node.removeChild(node.childNodes[x]);
					x--;
				}
				if (childNode.nodeType == 1)
				{
					cleanWhitespace(childNode);
				}
			}		
		}
	}
}
