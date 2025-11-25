import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Alert,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  Grid
} from '@mui/material';
import { Save as SaveIcon, ArrowBack } from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useServices } from '../../services/ServicesContext';
import TopBar from '../TopBar/TopBar';
import type { OptionalCourse } from '../../models/OptionalCourse';

interface CoursePackage {
  id: number;
  name: string;
  courses: OptionalCourse[];
}

interface PackagePreference {
  packageId: number;
  coursePreferences: { courseId: number; priority: number }[];
}

const StudentPreferences: React.FC = () => {
  const { user } = useAuth();
  const { allocationService } = useServices();
  const [packages, setPackages] = useState<CoursePackage[]>([]);
  const [preferences, setPreferences] = useState<PackagePreference[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    loadPackages();
  }, []);

  const loadPackages = async () => {
    if (!user?.academicYear || !user?.specialization) return;
    
    try {
      setLoading(true);
      const packagesData = await allocationService.getCoursePackages(
        user.academicYear,
        user.specialization
      );
      setPackages(packagesData);
    } catch (err) {
      setError('Failed to load course packages');
    } finally {
      setLoading(false);
    }
  };

  const handleCoursePreference = (packageId: number, courseId: number, priority: number) => {
    setPreferences(prev => {
      const packageIndex = prev.findIndex(p => p.packageId === packageId);
      
      if (packageIndex === -1) {
        // New package
        if (priority > 0) {
          return [...prev, { 
            packageId, 
            coursePreferences: [{ courseId, priority }] 
          }];
        }
        return prev;
      }
      
      // Update existing package
      const updatedPackage = { ...prev[packageIndex] };
      const courseIndex = updatedPackage.coursePreferences.findIndex(c => c.courseId === courseId);
      
      if (priority === 0) {
        // Remove course preference
        updatedPackage.coursePreferences = updatedPackage.coursePreferences.filter(c => c.courseId !== courseId);
      } else if (courseIndex === -1) {
        // Add new course preference
        updatedPackage.coursePreferences.push({ courseId, priority });
      } else {
        // Update existing course preference
        updatedPackage.coursePreferences[courseIndex].priority = priority;
      }
      
      const newPreferences = [...prev];
      if (updatedPackage.coursePreferences.length === 0) {
        newPreferences.splice(packageIndex, 1);
      } else {
        newPreferences[packageIndex] = updatedPackage;
      }
      
      return newPreferences;
    });
  };

  const getCoursePreference = (packageId: number, courseId: number): number => {
    const pkg = preferences.find(p => p.packageId === packageId);
    return pkg?.coursePreferences.find(c => c.courseId === courseId)?.priority || 0;
  };

  const getUsedPrioritiesInPackage = (packageId: number): number[] => {
    const pkg = preferences.find(p => p.packageId === packageId);
    return pkg?.coursePreferences.map(c => c.priority) || [];
  };

  const handleSave = async () => {
    // Check if all packages have complete preferences (all 3 courses ranked)
    const incompletePackages = packages.filter(pkg => {
      const packagePref = preferences.find(p => p.packageId === pkg.id);
      return !packagePref || packagePref.coursePreferences.length !== 3;
    });

    if (incompletePackages.length > 0) {
      setError(`Please rank all courses in Package ${incompletePackages.map(p => p.id).join(', ')}. Each package must have all 3 courses ranked (1, 2, 3).`);
      return;
    }

    try {
      setSaving(true);
      setError(null);
      await allocationService.saveStudentPreferences(preferences);
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      setError('Failed to save preferences');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" p={4}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <TopBar 
        title="Course Preferences"
        showRoleChip={true}
      />
      
      <Box p={3}>
        <Typography variant="h4" gutterBottom>
          Optional Course Package Preferences
        </Typography>
        
        <Typography variant="body1" color="textSecondary" sx={{ mb: 3 }}>
          Select one course from each package and assign priorities. Priority 1 is your most preferred package.
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {success && (
          <Alert severity="success" sx={{ mb: 2 }}>
            Preferences saved successfully!
          </Alert>
        )}

        <Grid container spacing={3} sx={{ mb: 3 }} justifyContent="center">
          {packages.map((pkg) => (
            <Grid item xs={12} sm={6} md={4} key={pkg.id}>
              <Card sx={{ height: '100%' }}>
                <CardContent>
                  <Typography variant="h6" gutterBottom color="primary">
                    Package {pkg.id}
                  </Typography>
                  
                  <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
                    Rank courses in order of preference (1 = most preferred)
                  </Typography>

                  {pkg.courses.map((course) => (
                    <Box key={course.id} sx={{ mb: 2 }}>
                      <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Box flex={1}>
                          <Typography variant="body2" fontWeight="medium">
                            {course.name}
                          </Typography>
                          <Typography variant="caption" color="textSecondary">
                            {course.code} • Max: {course.maxStudents}
                          </Typography>
                        </Box>
                        
                        <FormControl size="small" sx={{ minWidth: 80, ml: 1 }}>
                          <Select
                            value={getCoursePreference(pkg.id, course.id)}
                            onChange={(e) => handleCoursePreference(
                              pkg.id, 
                              course.id, 
                              Number(e.target.value)
                            )}
                            displayEmpty
                          >
                            <MenuItem value={0}>-</MenuItem>
                            {[1, 2, 3].map(priority => (
                              <MenuItem 
                                key={priority} 
                                value={priority}
                                disabled={
                                  getUsedPrioritiesInPackage(pkg.id).includes(priority) && 
                                  getCoursePreference(pkg.id, course.id) !== priority
                                }
                              >
                                {priority}
                              </MenuItem>
                            ))}
                          </Select>
                        </FormControl>
                      </Box>
                    </Box>
                  ))}
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>

        {preferences.length > 0 && (
          <Card sx={{ mb: 3, bgcolor: 'grey.50' }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Your Preferences Summary
              </Typography>
              {preferences.map((pkg) => {
                const packageData = packages.find(p => p.id === pkg.packageId);
                return (
                  <Box key={pkg.packageId} sx={{ mb: 2 }}>
                    <Typography variant="subtitle2" color="primary">
                      Package {pkg.packageId}:
                    </Typography>
                    <Box display="flex" flexWrap="wrap" gap={1} sx={{ ml: 2 }}>
                      {pkg.coursePreferences
                        .sort((a, b) => a.priority - b.priority)
                        .map((coursePref) => {
                          const course = packageData?.courses.find(c => c.id === coursePref.courseId);
                          return (
                            <Chip
                              key={coursePref.courseId}
                              label={`${coursePref.priority}. ${course?.name}`}
                              size="small"
                              variant="outlined"
                            />
                          );
                        })}
                    </Box>
                  </Box>
                );
              })}
            </CardContent>
          </Card>
        )}

        <Button
          variant="outlined"
          startIcon={<ArrowBack />}
          onClick={() => window.location.href = '/dashboard'}
          sx={{ mr: 2 }}
        >
          Back to Dashboard
        </Button>
        
        <Button
          variant="contained"
          size="large"
          startIcon={<SaveIcon />}
          onClick={handleSave}
          disabled={saving}
        >
          {saving ? <CircularProgress size={20} /> : 'Save Preferences'}
        </Button>
      </Box>
    </Box>
  );
};

export default StudentPreferences;
