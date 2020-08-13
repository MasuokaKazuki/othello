import React from 'react';
import {render} from 'react-dom';

class Board extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            player: null,
            status: null,
            pieces: [],
        };
    }

    componentDidMount() {
        fetch("http://localhost:8080/api/v1/board/")
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        isLoaded: true,
                        player: result.player,
                        status: result.status,
                        pieces: JSON.parse(result.pieces)
                    });
                },
                (error) => {
                    this.setState({
                        isLoaded: true,
                        error
                    });
                }
            )
    }

    render () {
        const { error, isLoaded, player, status, pieces } = this.state;
        if (error) {
            return <div>エラーが発生しました: {error.message}</div>;
        } else if (!isLoaded) {
            return <div>Loading...</div>;
        } else {
            return(
                <>
                <div className="main-content__board">
                    <table>
                        <tbody>
                        {(() => {
                            const items = [];
                            for (var i = 0; i < pieces.length; i++){
                                const cell = [];
                                for (var j = 0; j < pieces[i].length; j++){
                                    const cellContent = "";
                                    switch( pieces[i][j] ) {
                                        case 0:
                                            cellContent = <span className="circle-black"></span>;
                                            break;
                                        case 1:
                                            cellContent = <span className="circle-white"></span>;
                                            break;
                                    }
                                    cell.push(<td key={j}>{cellContent}</td>);
                                }
                                items.push(<tr key={i}>{cell}</tr>);
                            }
                            return items;
                        })()}
                        </tbody>
                    </table>
                </div>
                <p>{player}</p>
                <p>{status}</p>
                </>
            );
        }
    }
}

render(<Board/>, document.getElementById('board'));