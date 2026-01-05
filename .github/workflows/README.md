# GitHub Actions Workflows

This repository includes comprehensive GitHub Actions workflows for automated CI/CD, testing, and documentation.

## 🚀 Workflows

### 1. **CI Pipeline** (`.github/workflows/ci.yml`)
**Triggers**: Push to `main`/`develop`, Pull Requests to `main`/`develop`

**Features**:
- ✅ Java 17 setup with Temurin distribution
- ✅ Comprehensive test suite execution
- ✅ Code quality validation
- ✅ Build verification
- ✅ Test result reporting
- ✅ Artifact upload and retention

**Jobs**:
- `test`: Runs all tests with JUnit 5
- `build-and-publish`: Creates build artifacts (main branch only)
- `code-quality`: Validates code standards

### 2. **Release Pipeline** (`.github/workflows/release.yml`)
**Triggers**: Git tags starting with `v*`

**Features**:
- 🏷️ Automated version extraction from git tags
- 📦 Complete build artifact generation
- 📋 GitHub Release creation with descriptions
- 📚 Javadoc generation and inclusion
- 🔄 Artifact upload with long-term retention

**Release Artifacts**:
- `megalodonte-router-{version}.jar` - Main library
- `megalodonte-router-{version}-sources.jar` - Source code
- `megalodonte-router-{version}-javadoc.jar` - Documentation

### 3. **Code Analysis** (`.github/workflows/code-analysis.yml`)
**Triggers**: Push to `main`/`develop`, Pull Requests

**Features**:
- 🔍 SonarCloud code quality analysis
- 🛡️ Dependency vulnerability scanning
- 📊 Test coverage reporting with JaCoCo
- 📈 Code metrics and technical debt analysis

**Requirements**:
- `SONAR_TOKEN` secret for SonarCloud integration
- Optional: Configure SonarCloud project settings

### 4. **Documentation Pipeline** (`.github/workflows/docs.yml`)
**Triggers**: Push to `main`, Pull Requests to `main`

**Features**:
- 📖 Javadoc generation
- 🌐 GitHub Pages deployment (main branch only)
- 📊 Test report generation
- 📁 Documentation artifact management

**Deployment**:
- Javadoc published to: `https://{username}.github.io/{repository}/javadoc/`

## 📋 Setup Requirements

### Secrets (Repository Settings > Secrets and variables > Actions)
- `SONAR_TOKEN`: For SonarCloud integration (optional)
- `GITHUB_TOKEN`: Automatically provided by GitHub Actions

### Branch Protection Rules (Recommended)
For `main` branch:
- ✅ Require status checks to pass before merging
- ✅ Require branches to be up to date before merging
- ✅ Require pull request reviews

## 🔧 Usage

### Creating a Release
```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0
```

This automatically:
1. Triggers the release workflow
2. Updates version in build.gradle.kts
3. Runs all tests
4. Creates build artifacts
5. Generates GitHub Release
6. Uploads artifacts

### Local Development
```bash
# Run the same checks as CI
./gradlew test check build

# Generate coverage report
./gradlew jacocoTestReport

# Generate documentation
./gradlew javadoc
```

## 📊 Reports and Artifacts

### Test Reports
- **Location**: `build/reports/tests/test/index.html`
- **Format**: HTML with detailed test results
- **Retention**: 30 days in GitHub Actions artifacts

### Code Quality
- **SonarCloud**: Project dashboard with code metrics
- **Dependency Check**: Vulnerability scan reports
- **Coverage**: JaCoCo XML and HTML reports

### Documentation
- **Javadoc**: Generated API documentation
- **GitHub Pages**: Live documentation site
- **Retention**: 90 days for documentation artifacts

## 🚨 Troubleshooting

### Common Issues
1. **Java Version**: Ensure Java 17 is used locally
2. **Test Failures**: Check test reports in Actions artifacts
3. **Build Failures**: Review build logs for specific errors
4. **SonarCloud**: Configure project settings and add token

### Debugging
- Download workflow artifacts for detailed logs
- Check individual job outputs for specific failures
- Use `./gradlew --info` locally for verbose output

## 🔄 Workflow Status

[![CI](https://github.com/eliezer-software-enginner/megalodonte-router/actions/workflows/ci.yml/badge.svg)](https://github.com/eliezer-software-enginner/megalodonte-router/actions/workflows/ci.yml)
[![Code Analysis](https://github.com/eliezer-software-enginner/megalodonte-router/actions/workflows/code-analysis.yml/badge.svg)](https://github.com/eliezer-software-enginner/megalodonte-router/actions/workflows/code-analysis.yml)
[![Documentation](https://github.com/eliezer-software-enginner/megalodonte-router/actions/workflows/docs.yml/badge.svg)](https://github.com/eliezer-software-enginner/megalodonte-router/actions/workflows/docs.yml)

---

*This workflow suite ensures consistent code quality, comprehensive testing, and automated releases for the Megalodonte Router project.*