import { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Button,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Alert,
    Chip,
} from '@mui/material';
import type { StudentAllocation } from '../../models/StudentAllocation';
import { useServices } from '../../services/ServicesContext';
import './AdminAssignOptionalsPage.styles.scss';

export const AdminAssignOptionalsPage = () => {
    const { allocationService } = useServices();
    const [years, setYears] = useState<number[]>([]);
    const [specializations, setSpecializations] = useState<string[]>([]);
    const [selectedYear, setSelectedYear] = useState<number | ''>('');
    const [selectedSpecialization, setSelectedSpecialization] = useState<string>('');
    const [allocations, setAllocations] = useState<StudentAllocation[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [loadingDropdowns, setLoadingDropdowns] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchDropdownData = async () => {
            try {
                const [fetchedYears, fetchedSpecializations] = await Promise.all([
                    allocationService.getYears(),
                    allocationService.getSpecializations(),
                ]);
                setYears(fetchedYears);
                setSpecializations(fetchedSpecializations);
                if (fetchedYears.length > 0) {
                    setSelectedYear(fetchedYears[0]);
                }
                if (fetchedSpecializations.length > 0) {
                    setSelectedSpecialization(fetchedSpecializations[0]);
                }
            } catch (err) {
                setError('Failed to load dropdown options. Please refresh the page.');
                console.error('Dropdown fetch error:', err);
            } finally {
                setLoadingDropdowns(false);
            }
        };

        fetchDropdownData();
    }, []);

    const handleRunAlgorithm = async () => {
        if (selectedYear === '' || selectedSpecialization === '') {
            setError('Please select both year and specialization.');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const result = await allocationService.assignOptionals(
                selectedYear,
                selectedSpecialization
            );
            setAllocations(result);
        } catch (err) {
            setError('Failed to assign optional courses. Please try again.');
            console.error('Allocation error:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="admin-assign-optionals-page">
            <Box className="admin-assign-optionals-page__header">
                <Typography variant="h4" component="h1" gutterBottom>
                    Optional Subjects Allocation
                </Typography>
                <Typography variant="body1" color="text.secondary" paragraph>
                    Select year and specialization, then run the allocation algorithm
                </Typography>
            </Box>

            <Paper className="admin-assign-optionals-page__controls" elevation={2}>
                <Box className="admin-assign-optionals-page__controls-content">
                    <FormControl className="admin-assign-optionals-page__dropdown" disabled={loadingDropdowns}>
                        <InputLabel>Year</InputLabel>
                        <Select
                            value={selectedYear}
                            label="Year"
                            onChange={(e) => setSelectedYear(e.target.value as number)}
                        >
                            {years.map((year) => (
                                <MenuItem key={year} value={year}>
                                    Year {year}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <FormControl className="admin-assign-optionals-page__dropdown" disabled={loadingDropdowns}>
                        <InputLabel>Specialization</InputLabel>
                        <Select
                            value={selectedSpecialization}
                            label="Specialization"
                            onChange={(e) => setSelectedSpecialization(e.target.value)}
                        >
                            {specializations.map((spec) => (
                                <MenuItem key={spec} value={spec}>
                                    {spec}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <Button
                        variant="contained"
                        color="primary"
                        size="large"
                        onClick={handleRunAlgorithm}
                        disabled={loading || loadingDropdowns}
                        className="admin-assign-optionals-page__run-button"
                    >
                        {loadingDropdowns ? 'Loading...' : loading ? 'Allocating...' : 'Allocate optionals for students'}
                    </Button>
                </Box>
            </Paper>

            {error && (
                <Alert severity="error" className="admin-assign-optionals-page__alert">
                    {error}
                </Alert>
            )}

            {allocations.length > 0 && (
                <Paper className="admin-assign-optionals-page__results" elevation={2}>
                    <Typography variant="h5" gutterBottom className="admin-assign-optionals-page__results-title">
                        Allocation Results
                    </Typography>
                    <Typography variant="body2" color="text.secondary" paragraph>
                        {allocations.length} students assigned to optional courses
                    </Typography>

                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Student Number</TableCell>
                                    <TableCell>Name</TableCell>
                                    <TableCell>Email</TableCell>
                                    <TableCell>Assigned Courses</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {allocations.map((allocation) => (
                                    <TableRow key={allocation.student.id}>
                                        <TableCell>{allocation.student.studentNumber}</TableCell>
                                        <TableCell>
                                            {allocation.student.firstName} {allocation.student.lastName}
                                        </TableCell>
                                        <TableCell>{allocation.student.email}</TableCell>
                                        <TableCell>
                                            <Box className="admin-assign-optionals-page__courses">
                                                {allocation.assignedCourses.map((course) => (
                                                    <Chip
                                                        key={course.id}
                                                        label={`${course.code} - ${course.name}`}
                                                        color="primary"
                                                        variant="outlined"
                                                        size="small"
                                                    />
                                                ))}
                                            </Box>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Paper>
            )}
        </div>
    );
};
