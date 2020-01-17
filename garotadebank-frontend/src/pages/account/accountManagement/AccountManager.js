import React, {useEffect, useState} from 'react';
import './AccountManager.css';
import {useParams} from "react-router-dom";
import {CircularProgress} from "@material-ui/core";
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import moment from "moment";
import Button from "@material-ui/core/Button";
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import TextField from "@material-ui/core/TextField";
import NumberFormat from 'react-number-format';
import TableFooter from '@material-ui/core/TableFooter';
import TablePagination from '@material-ui/core/TablePagination';
import env from "../../../config/env";
import Container from "@material-ui/core/Container";

const valueFormatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

export default function AccountManager({ accountAPI }) {
    let cpf = '';
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [account, setAccount] = useState([]);
    const {accountNumber} = useParams();
    const [deposit, setDeposit] = useState();
    const [withdraw, setWithdraw] = useState();
    const [cpfFlow, setCpfFlow] = useState(false);

    const handleCpf = event => cpf = event.target.value;
    const handleDeposit = event => setDeposit(parseInt(event.target.value, 10));
    const handleWithdraw = event => setWithdraw(parseInt(event.target.value, 10));

    function sendCpf() {
        setLoading(true);
        accountAPI.addCpf(accountNumber, cpf).then(() => {
            setCpfFlow(false);
            setLoading(false);
        });
    }

    function doDeposit() {
        accountAPI.deposit(accountNumber, deposit).then(() => {
            account.events.unshift({
                payload: {
                    value: deposit,
                },
                name: "FundsDepositedEvent",
                timestamp: moment(),
            });
            account.summary.balance += deposit;
            setAccount({...account});
            setDeposit('');
        })
    }

    function doWithdraw() {
        accountAPI.withdraw(accountNumber, withdraw).then(() => {
            account.events.unshift({
                payload: {
                    value: withdraw,
                },
                name: "FundsWithdrawnEvent",
                timestamp: moment(),
            });
            account.summary.balance -= withdraw;
            setAccount({...account});
            setWithdraw('');
        })
    }

    useEffect( () => {
        async function fetchDashboard() {
            try {
                const account = await accountAPI.dashboard(accountNumber);
                setAccount(account);
                if (env.CPF_FLOW && account.summary.cpf !== undefined && account.summary.cpf.length === 0) {
                    console.log('entrou');
                    setCpfFlow(true);
                }
            } catch(e) {
                setError(e);
                console.log(e);
            }
            setLoading(false);

        }
        fetchDashboard()
    }, [accountAPI, accountNumber]);

    useEffect(() => console.log('render'));

    if (loading) return <CircularProgress />;
    if (error != null) return renderError(error);
    if (cpfFlow) {
        return (
            <Container className='AccountManager'>
                <form className="action cpf-input">
                    <TextField
                        label="Digite o seu CPF"
                        variant="outlined"
                        color="primary"
                        value={deposit}
                        onChange={handleCpf}
                    />
                    <Button className='action-button' variant="outlined" color="primary" onClick={sendCpf}>
                        Enviar
                    </Button>
                </form>
            </Container>
        )
    }

    const { summary } = account;
    return (
        <Container className='AccountManager'>
            {renderSummary(summary)}
            <div className="actions">
                <div className="action">
                    <TextField
                        label="Digite o valor a ser depositado"
                        variant="outlined"
                        color="primary"
                        value={deposit}
                        onChange={handleDeposit}
                        InputProps={{
                            inputComponent: NumberFormatCustom,
                        }}
                    />
                    <Button className='action-button' variant="outlined" color="primary" onClick={doDeposit}>
                        Depositar
                    </Button>
                </div>
                <div className="action">
                    <TextField
                        label="Digite o valor a ser sacado"
                        variant="outlined"
                        color="primary"
                        value={withdraw}
                        onChange={handleWithdraw}
                        InputProps={{
                            inputComponent: NumberFormatCustom,
                        }}/>
                    <Button className='action-button' variant="outlined" color="primary" onClick={doWithdraw}>Sacar</Button>
                </div>
            </div>
            <OperationsTable rows={account.events} />
        </Container>
    )
}

const formatDate = date => moment(date).format('DD/MM/YYYY HH:mm');

const eventNamesTranslation = {
    'FundsDepositedEvent': 'Depósito',
    'FundsWithdrawnEvent': 'Saque',
};

function renderSummary(summary) {
    return (
        <Card className='summary'>
            <CardContent className='summary__content'>
                <span><b>Cliente: </b>{summary.client}</span>
                {env.BALANCE_FEATURE &&
                <   span><b>Balanço: </b>{valueFormatter.format(summary.balance)}</span>
                }
                {env.ENABLE_BIGGEST_DEPOSIT_FEATURE &&
                    <div>
                        <span><b>Maior valor depositado: </b>{summary.biggestDeposit.value}</span>
                        <span><b>Data do maior valor depositado: </b>{formatDate(summary.biggestDeposit.date)}</span>
                    </div>
                }
                {env.CPF_FLOW &&
                    <span><b>CPF:</b> {summary.cpf}</span>
                }
            </CardContent>
        </Card>
    )
}

function OperationsTable({rows}) {
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = event => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    return (
        <TableContainer component={Paper} className='table'>
            <Table stickyHeader>
                <TableHead>
                    <TableRow>
                        <TableCell>Operação</TableCell>
                        <TableCell >Valor</TableCell>
                        <TableCell >Data</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {rows
                        .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                        .map((row, i) => (
                        <TableRow key={i}>
                            <TableCell >{eventNamesTranslation[row.name]}</TableCell>
                            <TableCell >{valueFormatter.format(row.payload.value)}</TableCell>
                            <TableCell >{formatDate(row.timestamp)}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
                <TableFooter>
                    <TableRow>
                        <TablePagination
                            rowsPerPage={rowsPerPage}
                            rowsPerPageOptions={[5]}
                            colSpan={3}
                            count={rows.length}
                            page={page}
                            SelectProps={{
                                native: true,
                            }}
                            onChangeRowsPerPage={handleChangeRowsPerPage}
                            onChangePage={handleChangePage}
                        />
                    </TableRow>
                </TableFooter>
            </Table>
        </TableContainer>
    );
}

function renderError(error) {
    // if (error.response.status === 404) {
        return <div>Conta não encontrada</div>
    // }
}

function NumberFormatCustom(props) {
    const { inputRef, onChange, ...other } = props;
    return (
        <NumberFormat
            {...other}
            getInputRef={inputRef}
            onValueChange={values => {
                onChange({
                    target: {
                        value: values.value,
                    },
                });
            }}
            thousandSeparator
            isNumericString
            allowNegative={false}
            prefix="R$ "
        />
    );
}
