import React from "react";

class LinedPaper extends React.Component {
    render() {
        return (
            <div class="card">
                <header>
                    <span class="card-title">{this.props.title}</span>
                </header>
                <p class="card-text">{this.props.text}</p>
            </div>
        );
    }
}

export default LinedPaper;
