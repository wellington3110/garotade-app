import React, {useEffect, useState} from 'react';
import {useParams} from "react-router-dom";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import TableFooter from "@material-ui/core/TableFooter";
import TablePagination from "@material-ui/core/TablePagination";
import moment from "moment";
import Container from "@material-ui/core/Container";

const formatDate = date => moment(date).format('DD/MM/YYYY HH:mm');

export default function Events({ eventsAPI }) {
    const {aggregateIdentifier} = useParams();
    const [events, setEvents] = useState([]);

    useEffect(() => {
        async function fetchEvents() {
            const r = await eventsAPI.events(aggregateIdentifier)
            console.log(r);
            setEvents(r);
        }
        fetchEvents();
    }, [aggregateIdentifier, eventsAPI]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = event => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    return (
        <Container>
            <TableContainer component={Paper} className='table'>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Name</TableCell>
                            <TableCell>Timestamp</TableCell>
                            <TableCell>Payload</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {events
                            .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                            .map((row, i) => (
                                <TableRow key={i}>
                                    <TableCell>{row.name}</TableCell>
                                    <TableCell>{formatDate(row.timestamp)}</TableCell>
                                    <TableCell>{JSON.stringify(row.payload, null, ' ')}</TableCell>
                                </TableRow>
                            ))}
                    </TableBody>
                    <TableFooter>
                        <TableRow>
                            <TablePagination
                                rowsPerPage={rowsPerPage}
                                colSpan={3}
                                count={events.length}
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
        </Container>
    );
}
