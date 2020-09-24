import React from "react";

class LinedPaper extends React.Component {
    render() {
        return (
            <div className="card">
                <header>
                    <span className="card-title">{this.props.title}</span>
                </header>
                <p className={this.props.shorten === true ? "card-text shorten-card-text" : "card-text"}>{this.props.text}</p>
            </div>
        );
    }
}

export default LinedPaper;
