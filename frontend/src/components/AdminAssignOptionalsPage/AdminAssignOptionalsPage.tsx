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
    AppBar,
    Toolbar,
} from '@mui/material';
import { Home as HomeIcon, Logout, AdminPanelSettings } from '@mui/icons-material';
import type { StudentAllocation } from '../../models/StudentAllocation';
import { useServices } from '../../services/ServicesContext';
import { useAuth } from '../../contexts/AuthContext';
import './AdminAssignOptionalsPage.styles.scss';

export const AdminAssignOptionalsPage = () => {
    const { allocationService } = useServices();
    const { user: userInfo, logout } = useAuth();
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

    const handleLogout = () => {
        logout();
    };

    const handleGoHome = () => {
        window.location.href = '/dashboard';
    };

    if (!userInfo) {
        return null;
    }

    return (
        <Box>
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        ElectiveMatch - Optional Subjects Allocation
                    </Typography>
                    
                    <Box display="flex" alignItems="center" gap={2}>
                        <Typography variant="body2">
                            {userInfo.firstName} {userInfo.lastName}
                        </Typography>
                        <Button 
                            color="inherit" 
                            onClick={handleGoHome}
                            startIcon={<HomeIcon />}
                        >
                            Home
                        </Button>
                        <Button 
                            color="inherit" 
                            onClick={handleLogout}
                            startIcon={<Logout />}
                        >
                            Logout
                        </Button>
                    </Box>
                </Toolbar>
            </AppBar>

            <Box p={3}>
                <Typography variant="h4" gutterBottom>
                    Optional Subjects Allocation
                </Typography>
                <Typography variant="body1" color="textSecondary" sx={{ mb: 3 }}>
                    Select year and specialization, then run the allocation algorithm
                </Typography>

                <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
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
                    <Paper sx={{ p: 3 }} elevation={2}>
                        <Typography variant="h5" gutterBottom>
                            Allocation Results
                        </Typography>
                        <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
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
                                                <Box display="flex" gap={1} flexWrap="wrap">
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
            </Box>
        </Box>
    );
};
