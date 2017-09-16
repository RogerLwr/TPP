
function actionBar_rightButtonCallback(name){
    log("callback",name);
    var button = actionBar.findRightButtonByName(name);
    if(button != null){
        button.onClick();
    }
}

function toast(text){
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('toast', text, null);
    })
}

function RightButton(){
    this.setText = function(title){
        var name = this.name;
        setupWebViewJavascriptBridge(function(bridge) {
            bridge.callHandler('__button_setText',{name:name,text:title}, null);
        })
    };

    this.setImage = function(src){

    }
}

function ActionBar(){
    this.rightButtons = new Array();

    this.hide = function(){
        setupWebViewJavascriptBridge(function(bridge) {
        	bridge.callHandler('__actionBar_hide', {}, null);
        })
    };

    this.show = function(){
        setupWebViewJavascriptBridge(function(bridge) {
            bridge.callHandler('__actionBar_show', {}, null);
        })
    };

    this.setTitle = function(title){
        setupWebViewJavascriptBridge(function(bridge) {
            bridge.callHandler('__actionBar_setTitle', title, null);
        })
    };

    this.setRightButtons = function(params){
        this.rightButtons = new Array();
        toast("params:" + params);
        for(var index in params){
            var param = params[index];
            var button = new RightButton();
            toast("param=" + param.name);
            button.name = param.name;
            button.text = param.text;
            button.onClick = param.onClick;
            toast(param.name);
            toast(button);
            this.rightButtons[param.name] = button;
        }
        setupWebViewJavascriptBridge(function(bridge) {
            bridge.callHandler('__actionBar_setRightButtons', params, null);
        });
        return this.rightButtons;
    };

    this.findRightButtonByName = function(name){
        var b = this.rightButtons[name];
        return b;
    }
}

var actionBar = new ActionBar();
