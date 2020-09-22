import React from "react";

class PaperStack extends React.Component {
    render() {
        if (!this.props.count || this.props.count < 1) {
            return null;
        }
        const paperStack = [];
        for (let i = 0; i < this.props.count; i++) {
            let style = {
                marginLeft: i > 0 ? "-10px" : "",
                zIndex: -1 * i,
                position: "relative"
            };
            paperStack.push(<img class="paper" src="../paper_icon.svg" alt="piece of paper" style={style} />);
        }

        return <span class="paper-stack">{paperStack}</span>;
    }
}

export default PaperStack;
