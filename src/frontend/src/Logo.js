import React from "react";

class Logo extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div id="logo" class="centered noselect">
                <span id="logo-text">Write</span>
                <span role="img" id="poop-emoji" aria-label="smiling poop">💩</span>
            </div>
        );
    }
}

export default Logo;