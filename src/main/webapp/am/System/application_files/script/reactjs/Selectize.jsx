// SimpleSelect = require("react-selectize").SimpleSelect
var HighligtingSelect =  React.createClass({


    // render :: a -> ReactElement
    render: function(){
        var self = this;
        return <SimpleSelect
            placeholder = {self.props.placeholder}

            // we use state for search, so we can access it inside the options map function below
            search = {this.state.search}
            onSearchChange = {function(search){
                self.setState({search: search});
            }}

            // the partitionString method from prelude-extension library has the following signature:
            // parititionString :: String -> String -> [[Int, Int, Boolean]]
            options = {
                this.props.optionList
            }
            renderValue = {function(item){
                /*var e=document.createElement('span');
                // set text
                jQuery(e).text(txt);
                // set font parameters
                jQuery(e).css({
                    'font-size': fontsize,
                    'font-family': fontname
                });*/
                // get
                return item.value.substring(0,15)+"...";
            }}
            // we add the search to the uid property of each option
            // to re-render it whenever the search changes
            // uid :: (Equatable e) => Item -> e
            uid = {function(item){
                return item.value + self.state.search;
            }}

            // here we use the HighlightedText component to render the result of partition-string
            // render-option :: Item -> ReactElement
           /* renderOption = {function(item){
                return (
                    <div>{item.label.substring(0, 20)+"..."}</div>
                    /*<div className = "simple-option">
                    <HighlightedText
                        partitions = {item.labelPartitions}
                        text = {item.label}
                        highlightStyle = {{
                            backgroundColor: "rgba(255,255,0,0.4)",
                            fontWeight: "bold"
                        }}
                    />
                </div>
                )
            }}*/
        />
    },

    // getInitialState :: a -> UIState
    getInitialState: function() {
        return {search: ""}
    }

});
window.HighligtingSelect = HighligtingSelect;